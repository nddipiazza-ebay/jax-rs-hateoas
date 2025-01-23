package com.jayway.jaxrs.hateoas.core.jersey;

import com.jayway.jaxrs.hateoas.CollectionWrapperStrategy;
import com.jayway.jaxrs.hateoas.HateoasContextProvider;
import com.jayway.jaxrs.hateoas.HateoasLinkInjector;
import com.jayway.jaxrs.hateoas.HateoasVerbosity;
import com.jayway.jaxrs.hateoas.HateoasViewFactory;
import com.jayway.jaxrs.hateoas.core.HateoasConfigurationFactory;
import com.jayway.jaxrs.hateoas.core.HateoasResponse.HateoasResponseBuilder;
import com.jayway.jaxrs.hateoas.web.RequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * JAX-RS Application adding HATEOAS capability to a Jersey application. Subclass this to add hypermedia capability
 * to your Jersey application.
 *
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 */
public abstract class JerseyHateoasApplication extends ResourceConfig {
    private static final Logger log = LoggerFactory.getLogger(JerseyHateoasApplication.class);

    /**
     * Creates a new Application with he given {@link HateoasVerbosity} level.
     *
     * @param verbosity the verbosity level to use in the application
     * @param props     properties to be passed to {@link ResourceConfig} constructor
     */
    public JerseyHateoasApplication(HateoasVerbosity verbosity,
                                    Map<String, Object> props) {
        this(HateoasConfigurationFactory.createLinkInjector(props),
                HateoasConfigurationFactory.createCollectionWrapperStrategy(props),
                verbosity,
                HateoasConfigurationFactory.createHateoasViewFactory(props, JerseyHateoasViewFactory.class.getName()));
    }

    /**
     * Creates a new Application based on Servlet init params. Note the HATEOAS
     * properties can be combined with standard Jersey properties defined in
     * {@link ResourceConfig}
     *
     * @see com.jayway.jaxrs.hateoas.core.HateoasConfigurationFactory#PROPERTY_HATEOAS_VERBOSITY
     * @see com.jayway.jaxrs.hateoas.core.HateoasConfigurationFactory#PROPERTY_HATEOAS_LINK_INJECTOR
     * @see com.jayway.jaxrs.hateoas.core.HateoasConfigurationFactory#PROPERTY_HATEOAS_COLLECTION_WRAPPER_STRATEGY
     * @see com.jayway.jaxrs.hateoas.core.HateoasConfigurationFactory#PROPERTY_HATEOAS_VIEW_FACTORY
     */
    public JerseyHateoasApplication(Map<String, Object> props) {
        this(HateoasConfigurationFactory.createLinkInjector(props),
                HateoasConfigurationFactory.createCollectionWrapperStrategy(props),
                HateoasConfigurationFactory.createVerbosity(props),
                HateoasConfigurationFactory.createHateoasViewFactory(props, JerseyHateoasViewFactory.class.getName()));
    }

    public JerseyHateoasApplication(HateoasLinkInjector<Object> linkInjector,
                                    CollectionWrapperStrategy collectionWrapperStrategy,
                                    HateoasVerbosity verbosity,
                                    HateoasViewFactory viewFactory) {
        Set<Class<?>> allClasses = getClasses();
        for (Class<?> clazz : allClasses) {
            HateoasContextProvider.getDefaultContext().mapClass(clazz);
        }

        HateoasResponseBuilder.configure(linkInjector, collectionWrapperStrategy, viewFactory);
        HateoasVerbosity.setDefaultVerbosity(verbosity);

        register((ContainerRequestFilter) containerRequestContext -> {
			RequestContext.clearRequestContext();
			RequestContext ctx = new RequestContext(UriBuilder.fromUri(containerRequestContext.getUriInfo().getBaseUri()), containerRequestContext.getHeaderString(RequestContext.HATEOAS_OPTIONS_HEADER));
			RequestContext.setRequestContext(ctx);
		});
        register((ContainerResponseFilter) (containerRequestContext, containerResponseContext) -> {
			RequestContext.clearRequestContext();
			RequestContext ctx = new RequestContext(UriBuilder.fromUri(containerRequestContext.getUriInfo().getBaseUri()), containerRequestContext.getHeaderString(RequestContext.HATEOAS_OPTIONS_HEADER));
			RequestContext.setRequestContext(ctx);
		});
    }

}
