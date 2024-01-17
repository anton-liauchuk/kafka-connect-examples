package com.uuidable.rest;

import java.util.UUID;

/**
 * Loads meta information for connector.
 */
public interface MetaInfoLoader {

	/**
	 * Identifier by connector name.
	 *
	 * @param connector connector name
	 * @return identifier.
	 */
	UUID load(String connector);

}
