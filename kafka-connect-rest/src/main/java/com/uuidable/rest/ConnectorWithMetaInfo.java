package com.uuidable.rest;

import java.util.Objects;
import java.util.UUID;

public class ConnectorWithMetaInfo {
	private final UUID id;
	private final String connector;

	public ConnectorWithMetaInfo(UUID id, String connector) {
		this.id = id;
		this.connector = connector;
	}

	public UUID getId() {
		return id;
	}

	public String getConnector() {
		return connector;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ConnectorWithMetaInfo that = (ConnectorWithMetaInfo) o;
		return Objects.equals(id, that.id) && Objects.equals(connector, that.connector);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, connector);
	}
}
