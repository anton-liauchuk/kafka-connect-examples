package com.uuidable.rest;

import java.util.Map;

import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.rest.ConnectRestExtension;
import org.apache.kafka.connect.rest.ConnectRestExtensionContext;

public class MetaInfoRestExtension implements ConnectRestExtension {

	@Override
	public void register(ConnectRestExtensionContext restPluginContext) {
		restPluginContext.configurable().register(new MetaInfoResource(restPluginContext.clusterState(), new RandomMetaInfoLoader()));
	}

	@Override
	public void close() {
	}

	@Override
	public void configure(Map<String, ?> configs) {
	}

	@Override
	public String version() {
		return AppInfoParser.getVersion();
	}
}
