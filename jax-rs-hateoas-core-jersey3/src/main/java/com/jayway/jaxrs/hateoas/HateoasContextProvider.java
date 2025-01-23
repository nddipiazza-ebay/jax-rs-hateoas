package com.jayway.jaxrs.hateoas;

/**
 * Factory for the default HateoasContext. Not intended for external use.
 *
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 */
public class HateoasContextProvider {
	private final static HateoasContext defaultContext = new DefaultHateoasContext();

	public static HateoasContext getDefaultContext() {
		return defaultContext;
	}
}
