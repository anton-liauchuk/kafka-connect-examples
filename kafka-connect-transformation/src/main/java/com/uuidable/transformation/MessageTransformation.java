package com.uuidable.transformation;

import java.util.Map;
import java.util.UUID;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.Requirements;

public class MessageTransformation<R extends ConnectRecord<R>> implements Transformation<R> {

	private static final String ID_FIELD = "id";
	private static final String NAME = "name";
	private static final String PURPOSE = "message model";
	private static final String NAME_DEFAULT_VALUE = "default_name";
	private static final String PAGEID_KEY = "pageid";

	@Override
	public R apply(R record) {
		var transformedSchema = SchemaBuilder.struct()
				.field(ID_FIELD, Schema.STRING_SCHEMA)
				.field(NAME, Schema.STRING_SCHEMA)
				.build();

		Schema schema = record.valueSchema();
		if (schema == null) {
			var transformed = new Struct(transformedSchema)
					.put(ID_FIELD, UUID.randomUUID().toString())
					.put(NAME, NAME_DEFAULT_VALUE);

			return record.newRecord(
					record.topic(),
					record.kafkaPartition(),
					Schema.STRING_SCHEMA,
					transformed.get(ID_FIELD),
					transformedSchema,
					transformed,
					record.timestamp()
			);
		} else {
			var value = Requirements.requireStruct(record.value(), PURPOSE);
			var transformed = new Struct(transformedSchema)
					.put(ID_FIELD, UUID.randomUUID().toString())
					.put(NAME, value.get(PAGEID_KEY).toString());

			return record.newRecord(
					record.topic(),
					record.kafkaPartition(),
					Schema.STRING_SCHEMA,
					transformed.get(ID_FIELD),
					transformedSchema,
					transformed,
					record.timestamp()
			);
		}
	}

	@Override
	public ConfigDef config() {
		return new ConfigDef();
	}

	@Override
	public void close() {

	}

	@Override
	public void configure(Map<String, ?> map) {

	}
}
