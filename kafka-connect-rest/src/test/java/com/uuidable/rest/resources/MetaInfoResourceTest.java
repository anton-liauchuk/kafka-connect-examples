package com.uuidable.rest.resources;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.kafka.connect.health.ConnectClusterState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uuidable.rest.ConnectorWithMetaInfo;
import com.uuidable.rest.MetaInfoLoader;
import com.uuidable.rest.MetaInfoResource;

@ExtendWith(MockitoExtension.class)
class MetaInfoResourceTest {

	public static final UUID CONNECTOR_1_ID = UUID.fromString("a5fe59f3-727a-4c22-8b2b-9aaa3ecc84e1");
	public static final UUID CONNECTOR_2_ID = UUID.fromString("ac25c054-b63d-4895-b50e-304d701d7c6d");
	public static final String CONNECTOR_1_NAME = "connector-1";
	public static final String CONNECTOR_2_NAME = "connector-2";
	@Mock
	ConnectClusterState clusterState;

	@Mock
	MetaInfoLoader metaInfoLoader;

	private MetaInfoResource metaInfoResource;

	@BeforeEach
	public void beforeEach() {
		metaInfoResource = new MetaInfoResource(clusterState, metaInfoLoader);
	}

	@Test
	void getMeta_twoConnectors_connectorsWithIds() {
		when(clusterState.connectors()).thenReturn(Arrays.asList(CONNECTOR_1_NAME, CONNECTOR_2_NAME));
		when(metaInfoLoader.load(ArgumentMatchers.eq(CONNECTOR_1_NAME))).thenReturn(CONNECTOR_1_ID);
		when(metaInfoLoader.load(ArgumentMatchers.eq(CONNECTOR_2_NAME))).thenReturn(CONNECTOR_2_ID);

		Response result = metaInfoResource.getMeta();

		Assertions.assertThat(result.getStatusInfo()).isEqualTo(Status.OK);
		Assertions.assertThat(result.hasEntity()).isTrue();
		Collection<ConnectorWithMetaInfo> connectors = (Collection<ConnectorWithMetaInfo>) result.getEntity();
		Assertions
				.assertThat(connectors)
				.hasSize(2)
				.containsExactlyInAnyOrder(new ConnectorWithMetaInfo(CONNECTOR_1_ID, CONNECTOR_1_NAME),
						new ConnectorWithMetaInfo(CONNECTOR_2_ID, CONNECTOR_2_NAME)
				);
	}
}