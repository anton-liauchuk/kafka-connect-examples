package com.uuidable.rest;

import java.util.Collection;
import java.util.stream.Collectors;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.kafka.connect.health.ConnectClusterState;

@Path("/meta")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MetaInfoResource {

	public final ConnectClusterState clusterState;
	public final MetaInfoLoader metaInfoLoader;

	public MetaInfoResource(ConnectClusterState clusterState, MetaInfoLoader metaInfoLoader) {
		this.clusterState = clusterState;
		this.metaInfoLoader = metaInfoLoader;
	}

	@GET
	public Response getMeta() {
		Collection<String> connectors = clusterState.connectors();
		var result = connectors.stream().map(connector -> {
			var id = metaInfoLoader.load(connector);
			return new ConnectorWithMetaInfo(id, connector);
		}).collect(Collectors.toList());
		return Response.ok(result).build();
	}
}
