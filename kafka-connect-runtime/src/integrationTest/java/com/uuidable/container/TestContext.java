package com.uuidable.container;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class TestContext {

	public static final TestContext INSTANCE = new TestContext();

	private static final String POSTGRES_IMAGE = "postgres:16-alpine";
	private static final String POSTGRES_SOURCE_NETWORK_ALIAS = "source";
	private static final int POSTGRES_INTERNAL_PORT = 5432;
	private static final String POSTGRES_SOURCE_DB_NAME = "test";
	private static final String LOCAL_INSTALL_DIR = "build/install/kafka-connect-runtime";
	private static final String KC_PLUGIN_DIR = "/test/kafka-connect";
	private static final String KAFKA_IMAGE = "confluentinc/cp-kafka:7.7.0";
	private static final String CONNECT_IMAGE = "confluentinc/cp-kafka-connect:7.7.0";
	private static final String POSTGRES_SOURCE_INTERNAL_CONNECTION_URL = String.format(
			"jdbc:postgresql://%s:%d/%s?loggerLevel=OFF",
			POSTGRES_SOURCE_NETWORK_ALIAS,
			POSTGRES_INTERNAL_PORT,
			POSTGRES_SOURCE_DB_NAME
	);
	private final Network network;
	private final ConfluentKafkaContainer kafka;
	private final KafkaConnectContainer kafkaConnect;
	private final PostgreSQLContainer<?> postgres;
	private final CustomerService customerService;

	private TestContext() {
		network = Network.newNetwork();
		kafka = new ConfluentKafkaContainer(DockerImageName.parse(KAFKA_IMAGE))
				.withNetwork(network);
		postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE)
				.withNetwork(network)
				.withNetworkAliases(POSTGRES_SOURCE_NETWORK_ALIAS)
				.withDatabaseName(POSTGRES_SOURCE_DB_NAME);
		kafkaConnect = new KafkaConnectContainer(DockerImageName.parse(CONNECT_IMAGE))
				.withNetwork(network)
				.dependsOn(kafka)
				.dependsOn(postgres)
				.withLabel("com.testcontainers.allow-filesystem-access", "true")
				.withCopyToContainer(MountableFile.forHostPath(LOCAL_INSTALL_DIR), KC_PLUGIN_DIR)
				.withEnv("CONNECT_PLUGIN_PATH", KC_PLUGIN_DIR)
				.withEnv("CONNECT_BOOTSTRAP_SERVERS", kafka.getNetworkAliases().get(0) + ":9093")
				.withEnv("CONNECT_OFFSET_FLUSH_INTERVAL_MS", "500");

		Startables.deepStart(Stream.of(kafka, kafkaConnect, postgres)).join();
		customerService = new CustomerService(new DBConnectionProvider(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()));

		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
	}

	private void shutdown() {
		kafkaConnect.close();
		kafka.close();
		network.close();
	}

	private String getLocalBootstrapServers() {
		return kafka.getBootstrapServers();
	}

	public void startConnector(KafkaConnectContainer.Config config) {
		kafkaConnect.startConnector(config);
		kafkaConnect.ensureConnectorRunning(config.getName());
	}

	public void stopConnector(String name) {
		kafkaConnect.stopConnector(name);
	}

	public KafkaProducer<String, String> initLocalProducer() {
		return new KafkaProducer<>(
				Map.of(
						ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
						getLocalBootstrapServers(),
						ProducerConfig.CLIENT_ID_CONFIG,
						UUID.randomUUID().toString()
				),
				new StringSerializer(),
				new StringSerializer()
		);
	}

	public Admin initLocalAdmin() {
		return Admin.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, getLocalBootstrapServers()));
	}

	public KafkaConsumer<String, String> getConsumer() {
		return new KafkaConsumer<>(
				Map.of(
						ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
						ConsumerConfig.GROUP_ID_CONFIG, "tc-" + UUID.randomUUID(),
						ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
				),
				new StringDeserializer(),
				new StringDeserializer()
		);
	}

	public void createCustomer() {
		customerService.createCustomer(new Customer(1L, "first"));
	}

	public String getConnectionUrl() {
		return POSTGRES_SOURCE_INTERNAL_CONNECTION_URL;
	}

	public String getUser() {
		return postgres.getUsername();
	}

	public String getPassword() {
		return postgres.getPassword();
	}
}
