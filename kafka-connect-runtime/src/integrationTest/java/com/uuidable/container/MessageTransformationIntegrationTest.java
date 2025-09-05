package com.uuidable.container;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageTransformationIntegrationTest extends IntegrationTestBase {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@BeforeEach
	public void before() {
		createTopic(testTopic, TEST_TOPIC_PARTITIONS);
	}

	@AfterEach
	public void after() {
		context.stopConnector(connectorName);
		deleteTopic(testTopic);
	}

	@Test
	public void transform_validMessage_transformed() throws JsonProcessingException {
		// given
		context.createCustomer();

		// when
		startConnector();
		var records = drain(context.getConsumer(), 1);

		// then
		for (ConsumerRecord<String, String> consumedRecord : records) {
			Map<String, Map<String, Object>> value = MAPPER.readValue(consumedRecord.value(), Map.class);
			var message = value.get("payload");

			Assertions.assertThat(message.get("id")).isNotNull();
			Assertions.assertThat(message.get("name")).isNotNull();
		}
	}

	private void startConnector() {
		KafkaConnectContainer.Config connectorConfig =
				new KafkaConnectContainer.Config(connectorName)
						.config("topics", testTopic)
						.config("tasks.max", 1)
						.config("key.converter", "org.apache.kafka.connect.json.JsonConverter")
						.config("value.converter", "org.apache.kafka.connect.json.JsonConverter")
						.config("name", connectorName)
						.config("connector.class", "io.confluent.connect.jdbc.JdbcSourceConnector")
						.config("mode", "incrementing")
						.config("connection.url", context.getConnectionUrl())
						.config("connection.user", context.getUser())
						.config("connection.password", context.getPassword())
						.config("incrementing.column.name", "id")
						.config("table.whitelist", "customers")
						.config("table.poll.interval.ms", "5000");

		context.startConnector(connectorConfig);
	}

	private List<ConsumerRecord<String, String>> drain(
			KafkaConsumer<String, String> consumer,
			int expectedRecordCount
	) {
		consumer.subscribe(List.of(testTopic));
		List<ConsumerRecord<String, String>> allRecords = new ArrayList<>();

		Unreliables.retryUntilTrue(
				30, TimeUnit.SECONDS, () -> {
					consumer.poll(Duration.ofMillis(50))
							.iterator()
							.forEachRemaining(allRecords::add);

					return allRecords.size() >= expectedRecordCount;
				}
		);

		return allRecords;
	}
}
