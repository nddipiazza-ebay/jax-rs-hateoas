package com.jayway.jaxrs.hateoas;

import java.util.Collection;

/**
 * Callback interface to be used for generating custom links for items.
 *
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 * @see com.jayway.jaxrs.hateoas.core.HateoasResponse.HateoasResponseBuilder#each(LinkProducer)
 */
public interface LinkProducer<T> {
    /**
     * Produce links for an item.
     *
     * @param entity the entity to produce links for.
     * @return one or more links to be added for the entity.
     */
	Collection<HateoasLink> getLinks(T entity);
}
