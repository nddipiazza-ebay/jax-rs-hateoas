package com.jayway.jaxrs.hateoas.web;

import jakarta.ws.rs.core.UriBuilder;

/**
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 */
public class RequestContext {

    public static final String HATEOAS_OPTIONS_HEADER = "x-jax-rs-hateoas-options";

    private final static ThreadLocal<RequestContext> currentContext = new ThreadLocal<RequestContext>();

    public static void setRequestContext(RequestContext context) {
        currentContext.set(context);
    }

    public static RequestContext getRequestContext() {
        return currentContext.get();
    }

    public static void clearRequestContext() {
        currentContext.remove();
    }


    private final UriBuilder basePath;

    private final String verbosityHeader;

    public RequestContext(UriBuilder basePath, String verbosityHeader) {
        this.basePath = basePath;
        this.verbosityHeader = verbosityHeader;
    }

    public UriBuilder getBasePath() {
        return basePath.clone();
    }

    public String getVerbosityHeader() {
        return verbosityHeader;
    }
}
