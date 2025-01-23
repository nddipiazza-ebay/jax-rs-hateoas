package com.jayway.jaxrs.hateoas.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.UriBuilder;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 */
public class RequestContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {


        final HttpServletRequest servletRequest = (HttpServletRequest) request;


        String requestURI = servletRequest.getRequestURI();
        requestURI = StringUtils.removeStart(requestURI, servletRequest.getContextPath() + servletRequest.getServletPath());
        String baseURL = StringUtils.removeEnd(servletRequest.getRequestURL().toString(), requestURI);
        UriBuilder uriBuilder = UriBuilder.fromUri(baseURL);

        RequestContext ctx = new RequestContext(uriBuilder, servletRequest.getHeader(RequestContext.HATEOAS_OPTIONS_HEADER));

        RequestContext.setRequestContext(ctx);
        try {
            chain.doFilter(request, response);
        } finally {
            RequestContext.clearRequestContext();
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
}
