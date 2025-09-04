package com.uuidable.container;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class IntegrationTestBase {
	protected static final int TEST_TOPIC_PARTITIONS = 2;
	protected final TestContext context = TestContext.INSTANCE;
	protected Admin admin;
	protected String connectorName;
	protected String testTopic;
	private KafkaProducer<String, String> producer;

	@BeforeEach
	public void baseBefore() {
		producer = context.initLocalProducer();
		admin = context.initLocalAdmin();

		this.connectorName = "test_connector-" + UUID.randomUUID();
		this.testTopic = "customers";
	}

	@AfterEach
	public void baseAfter() {
		producer.close();
		admin.close();
	}

	protected void createTopic(String topicName, int partitions) {
		try {
			admin
					.createTopics(List.of(new NewTopic(topicName, partitions, (short) 1)))
					.all()
					.get(10, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

	protected void deleteTopic(String topicName) {
		try {
			admin.deleteTopics(List.of(topicName)).all().get(10, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

	protected void flush() {
		producer.flush();
	}
}
