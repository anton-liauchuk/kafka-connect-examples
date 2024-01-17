package com.uuidable.rest;

import java.util.UUID;

public class RandomMetaInfoLoader implements MetaInfoLoader {

	@Override
	public UUID load(String connector) {
		return UUID.randomUUID();
	}
}
