package com.jayway.jaxrs.hateoas.core;

import com.jayway.jaxrs.hateoas.CollectionWrapperStrategy;
import com.jayway.jaxrs.hateoas.HateoasContextProvider;
import com.jayway.jaxrs.hateoas.HateoasLinkInjector;
import com.jayway.jaxrs.hateoas.HateoasVerbosity;
import com.jayway.jaxrs.hateoas.core.HateoasResponse.HateoasResponseBuilder;
import com.jayway.jaxrs.hateoas.support.DefaultCollectionWrapperStrategy;
import com.jayway.jaxrs.hateoas.support.DefaultHateoasViewFactory;
import com.jayway.jaxrs.hateoas.support.StrategyBasedLinkInjector;
import jakarta.ws.rs.core.Application;

import java.util.Set;

/**
 * Plain JAX-RS application adding HATEOAS capability. Subclass this to add hypermedia capability to a plain JAX-RS
 * application.
 *
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 */
public class HateoasApplication extends Application {
	public HateoasApplication() {
		this(HateoasVerbosity.MAXIMUM);
	}

	public HateoasApplication(HateoasVerbosity verbosity) {
		this(new StrategyBasedLinkInjector(), new DefaultCollectionWrapperStrategy(), verbosity);
	}

	public HateoasApplication(HateoasLinkInjector<Object> linkInjector,
                              CollectionWrapperStrategy collectionWrapperStrategy, HateoasVerbosity verbosity) {

		Set<Class<?>> allClasses = getClasses();
		for (Class<?> clazz : allClasses) {
			HateoasContextProvider.getDefaultContext().mapClass(clazz);
		}

		HateoasResponseBuilder.configure(linkInjector, collectionWrapperStrategy, new DefaultHateoasViewFactory());
        HateoasVerbosity.setDefaultVerbosity(verbosity);
	}
}
