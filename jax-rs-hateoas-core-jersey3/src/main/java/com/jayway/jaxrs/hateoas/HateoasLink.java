package com.jayway.jaxrs.hateoas;

import java.util.Map;

/**
 * Represents all information about a link to be included in a response. Not intended for external use.
 */
public interface HateoasLink {

	String getRel();

	String getMethod();

	String getId();

	String getHref();

	String[] getConsumes();

	String[] getProduces();

	String getLabel();

	String getDescription();

	Class<?> getTemplateClass();

	Map<String, Object> toMap(HateoasVerbosity verbosity);

}