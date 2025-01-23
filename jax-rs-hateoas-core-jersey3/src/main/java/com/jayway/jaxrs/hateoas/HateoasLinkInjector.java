package com.jayway.jaxrs.hateoas;

/**
 * Strategy interface for link injection.
 *
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 * @see com.jayway.jaxrs.hateoas.support.JavassistHateoasLinkInjector
 * @see com.jayway.jaxrs.hateoas.support.ReflectionBasedHateoasLinkInjector
 */
public interface HateoasLinkInjector<T> {
    
    boolean canInject(T entity);
    
	T injectLinks(T entity, LinkProducer<T> linkProducer, HateoasVerbosity verbosity);
}
