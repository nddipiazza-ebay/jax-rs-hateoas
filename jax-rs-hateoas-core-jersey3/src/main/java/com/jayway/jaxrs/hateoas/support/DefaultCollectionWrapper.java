package com.jayway.jaxrs.hateoas.support;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.jayway.jaxrs.hateoas.HateoasLinkBean;
import com.jayway.jaxrs.hateoas.HateoasLinkInjector;
import com.jayway.jaxrs.hateoas.HateoasVerbosity;
import com.jayway.jaxrs.hateoas.LinkProducer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Wrapper class for collection entities to enable root links in addition to the actual collection. Not for use
 * outside of the framework.
 *
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 */
public class DefaultCollectionWrapper<T> implements Iterable<T>, HateoasLinkBean {
    public final static String ROWS_FIELD_NAME = "rows";

    private Collection<T> rows;
    private Collection<Map<String, Object>> links;

    public DefaultCollectionWrapper() {
    }

    public DefaultCollectionWrapper(Collection<T> originalCollection) {
        rows = originalCollection;
    }

    public Collection<T> getRows() {
        return rows;
    }

    public void setRows(Collection<T> rows) {
        this.rows = rows;
    }

    @Override
    public Collection<Map<String, Object>> getLinks() {
        return links;
    }

    @Override
    public void setLinks(Collection<Map<String, Object>> links) {
        this.links = links;
    }

    @Override
    public Iterator<T> iterator() {
        return rows.iterator();
    }

    public void transformRows(final HateoasLinkInjector<T> linkInjector,
                              final LinkProducer<T> linkProducer, final HateoasVerbosity verbosity) {
        rows = Collections2.transform(rows, new Function<T, T>() {
            @Override
            public T apply(T from) {
                return linkInjector.injectLinks(from, linkProducer, verbosity);
            }
        });
    }
}
