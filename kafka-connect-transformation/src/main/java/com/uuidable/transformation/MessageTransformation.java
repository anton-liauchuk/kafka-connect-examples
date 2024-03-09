package com.uuidable.transformation;

import java.util.Map;
import java.util.UUID;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.Requirements;

public class MessageTransformation<R extends ConnectRecord<R>> implements Transformation<R> {

	private static final String PURPOSE = "message model";
	private static final String NAME_DEFAULT_VALUE = "default_name";
	private static final String FIELD_NAME_KEY = "name";

	@Override
	public R apply(R record) {
		Schema schema = record.valueSchema();
		if (schema == null) {
			var message = new Message();
			message.setId(UUID.randomUUID());
			message.setName(NAME_DEFAULT_VALUE);
			return record.newRecord(
					record.topic(),
					record.kafkaPartition(),
					record.keySchema(),
					message.getId(),
					null,
					message,
					record.timestamp()
			);
		} else {
			var value = Requirements.requireStruct(record.value(), PURPOSE);
			var message = new Message();
			message.setId(UUID.randomUUID());
			message.setName(value.get(FIELD_NAME_KEY).toString());

			return record.newRecord(
					record.topic(),
					record.kafkaPartition(),
					record.keySchema(),
					message.getId(),
					null,
					message,
					record.timestamp()
			);
		}
	}

	@Override
	public ConfigDef config() {
		return null;
	}

	@Override
	public void close() {

	}

	@Override
	public void configure(Map<String, ?> map) {

	}
}
