package com.jayway.jaxrs.hateoas;

/**
 * Keeps track of all methods annotated with {@link Linkable}, along with the metadata associated with each method.
 * Not intended for external use.
 *
 *
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 */
public interface HateoasContext {

	void mapClass(Class<?> clazz);

	LinkableInfo getLinkableInfo(String link);

}