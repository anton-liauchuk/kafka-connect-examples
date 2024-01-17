package com.uuidable.rest;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
