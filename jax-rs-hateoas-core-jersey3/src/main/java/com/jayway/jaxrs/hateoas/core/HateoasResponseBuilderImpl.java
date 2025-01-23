package com.jayway.jaxrs.hateoas.core;

import com.jayway.jaxrs.hateoas.CollectionWrapperStrategy;
import com.jayway.jaxrs.hateoas.HateoasLink;
import com.jayway.jaxrs.hateoas.HateoasLinkInjector;
import com.jayway.jaxrs.hateoas.HateoasVerbosity;
import com.jayway.jaxrs.hateoas.LinkProducer;
import com.jayway.jaxrs.hateoas.ParamExpander;
import com.jayway.jaxrs.hateoas.core.HateoasResponse.HateoasResponseBuilder;
import com.jayway.jaxrs.hateoas.support.AtomRels;
import com.jayway.jaxrs.hateoas.support.FieldPath;
import com.jayway.jaxrs.hateoas.support.ReflectionUtils;
import com.jayway.jaxrs.hateoas.web.RequestContext;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Variant;
import org.apache.commons.lang.NotImplementedException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.jayway.jaxrs.hateoas.core.HateoasResponseImpl.toStatusType;

/**
 * Default implementation of {@link HateoasResponseBuilder}.
 *
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 */
public class HateoasResponseBuilderImpl extends HateoasResponse.HateoasResponseBuilder {
    private Response.StatusType statusType = Response.Status.NO_CONTENT;
    private MultivaluedMap<String, Object> headers;
    private Object entity;
    private Type entityType;
    private final Map<FieldPath, ChainedLinkProducer> linkMappings = new HashMap<>();

    @Override
    public HateoasResponseBuilder link(String id, String rel, Object... params) {
        return links(HateoasResponseBuilder.makeLink(id, rel, params));
    }

    @Override
    public HateoasResponseBuilder link(FieldPath fieldPath, String id, String rel, String... entityFields) {
        return link(fieldPath, new ReflectionBasedLinkProducer(id, rel, entityFields));
    }

    @Override
    public HateoasResponseBuilder selfLink(FieldPath fieldPath, String id, String... entityFields) {
        return link(fieldPath, id, AtomRels.SELF, entityFields);
    }

    @Override
    public HateoasResponseBuilder link(FieldPath fieldPath, LinkProducer<?> linkProducer) {
        if (!linkMappings.containsKey(fieldPath)) {
            linkMappings.put(fieldPath, new ChainedLinkProducer());
        }
        ChainedLinkProducer chainedLinkProducer = linkMappings.get(fieldPath);
        chainedLinkProducer.append(linkProducer);
        return this;
    }

    @Override
    public HateoasResponseBuilder selfLink(String id, Object... params) {
        return link(id, AtomRels.SELF, params);
    }

    @Override
    public HateoasResponseBuilder each(String id, String rel, String... entityFields) {
        return each(new ReflectionBasedLinkProducer(id, rel, entityFields));
    }

    @Override
    public HateoasResponseBuilder selfEach(String id, String... entityFields) {
        return each(id, AtomRels.SELF, entityFields);
    }

    @Override
    public HateoasResponseBuilder links(HateoasLink... links) {
        return link(FieldPath.EMPTY_PATH, new FixedLinkProducer(Arrays.asList(links)));
    }

    @Override
    public HateoasResponseBuilder each(LinkProducer<?> linkProducer) {
        CollectionWrapperStrategy collectionWrapperStrategy = HateoasResponseBuilder.getCollectionWrapperStrategy();
        return link(FieldPath.path(collectionWrapperStrategy.rowsFieldName()), linkProducer);
    }

    @Override
    public HateoasResponseBuilder link(FieldPath fieldPath, String id, String rel, ParamExpander... paramExpanders) {
        return link(fieldPath, new ParamExpandingLinkProducer(id, rel, paramExpanders));
    }

    @Override
    public HateoasResponseBuilder selfLink(FieldPath fieldPath, String id, ParamExpander... paramExpanders) {
        return link(fieldPath, id, AtomRels.SELF, paramExpanders);
    }

    @Override
    public HateoasResponseBuilder selfLink(String id, ParamExpander... paramExpanders) {
        return link(id, AtomRels.SELF, paramExpanders);
    }

    @Override
    public HateoasResponseBuilder link(String id, String rel, ParamExpander... paramExpanders) {
        ParamExpandingLinkProducer linkProducer = new ParamExpandingLinkProducer(id, rel, paramExpanders);
        return links(linkProducer.getLinks(entity).toArray(new HateoasLink[0]));
    }

    @Override
    public HateoasResponseBuilder each(String id, String rel, ParamExpander... paramExpanders) {
        return each(new ParamExpandingLinkProducer(id, rel, paramExpanders));
    }

    @Override
    public HateoasResponseBuilder selfEach(String id, ParamExpander... paramExpanders) {
        return each(id, AtomRels.SELF, paramExpanders);
    }

    public HateoasResponseBuilderImpl() {
    }

    private HateoasResponseBuilderImpl(HateoasResponseBuilderImpl that) {
        this.statusType = that.statusType;
        this.entity = that.entity;
        if (that.headers != null) {
            this.headers = new MultivaluedHashMap<>(that.headers);
        } else {
            this.headers = null;
        }
        this.entityType = that.entityType;
    }

    public HateoasResponseBuilder entityWithType(Object entity, Type entityType) {
        this.entity = entity;
        this.entityType = entityType;
        return this;
    }

    private MultivaluedMap<String, Object> getHeaders() {
        if (headers == null)
            headers = new MultivaluedHashMap<>();
        return headers;
    }

    @SuppressWarnings("unchecked")
    public HateoasResponse build() {
        HateoasLinkInjector<Object> linkInjector = HateoasResponseBuilder.getLinkInjector();
        CollectionWrapperStrategy collectionWrapperStrategy = HateoasResponseBuilder.getCollectionWrapperStrategy();
        HateoasVerbosity verbosity = HateoasVerbosity.valueOf(RequestContext.getRequestContext().getVerbosityHeader());

        Object newEntity = entity;
        if (entity != null) {
            if (Collection.class.isAssignableFrom(entity.getClass())) {
                newEntity = collectionWrapperStrategy.wrapRootCollection((Collection<Object>) entity);
            }

            Set<Entry<FieldPath, ChainedLinkProducer>> entries = linkMappings.entrySet();
            for (Entry<FieldPath, ChainedLinkProducer> entry : entries) {
                newEntity = entry.getKey().injectLinks(newEntity, linkInjector, entry.getValue(), verbosity);
            }
        }

        final HateoasResponse r = new HateoasResponseImpl(statusType, getHeaders(), newEntity, entityType);
        reset();
        return r;
    }

    public HateoasResponse render(String template) {
        HateoasLinkInjector<Object> linkInjector = HateoasResponseBuilder.getLinkInjector();
        CollectionWrapperStrategy collectionWrapperStrategy = HateoasResponseBuilder.getCollectionWrapperStrategy();
        HateoasVerbosity verbosity = HateoasVerbosity.valueOf(RequestContext.getRequestContext().getVerbosityHeader());

        Object newEntity = entity;
        if (entity != null) {
            if (Collection.class.isAssignableFrom(entity.getClass())) {
                newEntity = collectionWrapperStrategy.wrapRootCollection((Collection<Object>) entity);
            }

            Set<Entry<FieldPath, ChainedLinkProducer>> entries = linkMappings.entrySet();
            for (Entry<FieldPath, ChainedLinkProducer> entry : entries) {
                newEntity = entry.getKey().injectLinks(newEntity, linkInjector, entry.getValue(), verbosity);
            }
        }

        final HateoasResponse r = new HateoasResponseImpl(statusType, getHeaders(), HateoasResponseBuilder.getViewFactory().createView(template, newEntity), entityType);
        reset();
        return r;
    }

    private void reset() {
        statusType = Response.Status.NO_CONTENT;
        headers = null;
        entity = null;
        entityType = null;
        linkMappings.clear();
    }

    @Override
    public HateoasResponseBuilder clone() {
        return new HateoasResponseBuilderImpl(this);
    }

    public HateoasResponseBuilder status(jakarta.ws.rs.core.Response.StatusType status) {
        if (status == null)
            throw new IllegalArgumentException();
        this.statusType = status;
        return this;
    }

    public HateoasResponseBuilder status(int status) {
        return status(toStatusType(status));
    }

    @Override
    public Response.ResponseBuilder status(int i, String s) {
        return status(i);
    }

    public HateoasResponseBuilder entity(Object entity) {
        this.entity = entity;
        this.entityType = (entity != null) ? entity.getClass() : null;
        return this;
    }

    @Override
    public Response.ResponseBuilder entity(Object o, Annotation[] annotations) {
        return entity(0);
    }

    @Override
    public Response.ResponseBuilder allow(String... strings) {
        return this;
    }

    @Override
    public Response.ResponseBuilder allow(Set<String> set) {
        return this;
    }

    public HateoasResponseBuilder type(jakarta.ws.rs.core.MediaType type) {
        headerSingle(jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE, type);
        return this;
    }

    public HateoasResponseBuilder type(String type) {
        return type(type == null ? null : jakarta.ws.rs.core.MediaType.valueOf(type));
    }

    public HateoasResponseBuilder variant(jakarta.ws.rs.core.Variant variant) {
        if (variant == null) {
            type((jakarta.ws.rs.core.MediaType) null);
            language((String) null);
            encoding(null);
            return this;
        }

        type(variant.getMediaType());
        language(variant.getLanguage());
        encoding(variant.getEncoding());

        return this;
    }

    public HateoasResponseBuilder variants(List<jakarta.ws.rs.core.Variant> variants) {
        if (variants == null) {
            header(jakarta.ws.rs.core.HttpHeaders.VARY, null);
            return this;
        }

        if (variants.isEmpty())
            return this;

        jakarta.ws.rs.core.MediaType accept = variants.get(0).getMediaType();
        boolean vAccept = false;

        Locale acceptLanguage = variants.get(0).getLanguage();
        boolean vAcceptLanguage = false;

        String acceptEncoding = variants.get(0).getEncoding();
        boolean vAcceptEncoding = false;

        for (Variant v : variants) {
            vAccept |= !vAccept && vary(v.getMediaType(), accept);
            vAcceptLanguage |= !vAcceptLanguage && vary(v.getLanguage(), acceptLanguage);
            vAcceptEncoding |= !vAcceptEncoding && vary(v.getEncoding(), acceptEncoding);
        }

        StringBuilder vary = new StringBuilder();
        append(vary, vAccept, jakarta.ws.rs.core.HttpHeaders.ACCEPT);
        append(vary, vAcceptLanguage, jakarta.ws.rs.core.HttpHeaders.ACCEPT_LANGUAGE);
        append(vary, vAcceptEncoding, jakarta.ws.rs.core.HttpHeaders.ACCEPT_ENCODING);

        if (vary.length() > 0)
            header(jakarta.ws.rs.core.HttpHeaders.VARY, vary.toString());
        return this;
    }

    @Override
    public Response.ResponseBuilder links(Link... links) {
        throw new NotImplementedException();
    }

    @Override
    public Response.ResponseBuilder link(URI uri, String s) {
        throw new NotImplementedException();
    }

    @Override
    public Response.ResponseBuilder link(String s, String s1) {
        throw new NotImplementedException();
    }

    private boolean vary(jakarta.ws.rs.core.MediaType v, MediaType vary) {
        return v != null && !v.equals(vary);
    }

    private boolean vary(Locale v, Locale vary) {
        return v != null && !v.equals(vary);
    }

    private boolean vary(String v, String vary) {
        return v != null && !v.equalsIgnoreCase(vary);
    }

    private void append(StringBuilder sb, boolean v, String s) {
        if (v) {
            if (sb.length() > 0)
                sb.append(',');
            sb.append(s);
        }
    }

    public HateoasResponseBuilder language(String language) {
        headerSingle(jakarta.ws.rs.core.HttpHeaders.CONTENT_LANGUAGE, language);
        return this;
    }

    public HateoasResponseBuilder language(Locale language) {
        headerSingle(jakarta.ws.rs.core.HttpHeaders.CONTENT_LANGUAGE, language);
        return this;
    }

    public HateoasResponseBuilder location(URI location) {
        headerSingle(jakarta.ws.rs.core.HttpHeaders.LOCATION, location);
        return this;
    }

    public HateoasResponseBuilder location(HateoasLink location) {
        headerSingle(jakarta.ws.rs.core.HttpHeaders.LOCATION, location.getHref());
        return this;
    }

    public HateoasResponseBuilder contentLocation(URI location) {
        headerSingle(jakarta.ws.rs.core.HttpHeaders.CONTENT_LOCATION, location);
        return this;
    }

    public Response.ResponseBuilder encoding(String encoding) {
        headerSingle(jakarta.ws.rs.core.HttpHeaders.CONTENT_ENCODING, encoding);
        return this;
    }

    public HateoasResponseBuilder tag(jakarta.ws.rs.core.EntityTag tag) {
        headerSingle(jakarta.ws.rs.core.HttpHeaders.ETAG, tag);
        return this;
    }

    public HateoasResponseBuilder tag(String tag) {
        return tag(tag == null ? null : new EntityTag(tag));
    }

    @Override
    public Response.ResponseBuilder variants(Variant... variants) {
        return null;
    }

    public HateoasResponseBuilder lastModified(Date lastModified) {
        headerSingle(jakarta.ws.rs.core.HttpHeaders.LAST_MODIFIED, lastModified);
        return this;
    }

    public HateoasResponseBuilder cacheControl(CacheControl cacheControl) {
        headerSingle(jakarta.ws.rs.core.HttpHeaders.CACHE_CONTROL, cacheControl);
        return this;
    }

    public HateoasResponseBuilder expires(Date expires) {
        headerSingle(jakarta.ws.rs.core.HttpHeaders.EXPIRES, expires);
        return this;
    }

    public HateoasResponseBuilder cookie(jakarta.ws.rs.core.NewCookie... cookies) {
        if (cookies != null) {
            for (NewCookie cookie : cookies)
                header(jakarta.ws.rs.core.HttpHeaders.SET_COOKIE, cookie);
        } else {
            header(HttpHeaders.SET_COOKIE, null);
        }
        return this;
    }

    public HateoasResponseBuilder header(String name, Object value) {
        return header(name, value, false);
    }

    @Override
    public Response.ResponseBuilder replaceAll(MultivaluedMap<String, Object> multivaluedMap) {
        return null;
    }

    public HateoasResponseBuilder headerSingle(String name, Object value) {
        return header(name, value, true);
    }

    public HateoasResponseBuilder header(String name, Object value, boolean single) {
        if (value != null) {
            if (single) {
                getHeaders().putSingle(name, value);
            } else {
                getHeaders().add(name, value);
            }
        } else {
            getHeaders().remove(name);
        }
        return this;
    }

    private final static class ParamExpandingLinkProducer implements LinkProducer<Object> {

        private final String id;
        private final String rel;
        private final ParamExpander[] paramExpanders;

        private ParamExpandingLinkProducer(String id, String rel, ParamExpander... paramExpanders) {
            this.id = id;
            this.rel = rel;
            this.paramExpanders = paramExpanders;
        }

        @Override
        public Collection<HateoasLink> getLinks(Object entity) {
            Map<String, Object> queryParams = new LinkedHashMap<String, Object>();

            LinkedList<Object> argumentList = new LinkedList<Object>();

            for (ParamExpander paramExpander : paramExpanders) {

                if (paramExpander instanceof ParamExpander.QueryParamExpander) {
                    ParamExpander.QueryParamExpander expander = (ParamExpander.QueryParamExpander) paramExpander;
                    queryParams.put(expander.getName(), expander.getValue());

                } else if (paramExpander instanceof ParamExpander.ReflectionPathParamExpander) {

                    ParamExpander.ReflectionPathParamExpander expander = (ParamExpander.ReflectionPathParamExpander) paramExpander;
                    Object fieldValue = ReflectionUtils.getFieldValueHierarchical(entity, expander.getField());
                    argumentList.add(fieldValue);

                } else if (paramExpander instanceof ParamExpander.PathParamExpander) {

                    ParamExpander.PathParamExpander expander = (ParamExpander.PathParamExpander) paramExpander;
                    argumentList.add(expander.getValue());

                }
            }
            return Collections.singletonList(makeLink(id, rel, queryParams, argumentList.toArray()));
        }
    }

    private final static class ReflectionBasedLinkProducer implements LinkProducer<Object> {
        private final String id;
        private final String rel;
        private final String[] entityFields;

        private ReflectionBasedLinkProducer(String id, String rel, String... entityFields) {
            this.id = id;
            this.rel = rel;
            this.entityFields = entityFields;
        }

        @Override
        public Collection<HateoasLink> getLinks(Object entity) {
            LinkedList<Object> argumentList = new LinkedList<Object>();

            for (String fieldName : entityFields) {
                Object fieldValue = ReflectionUtils.getFieldValueHierarchical(entity, fieldName);
                argumentList.add(fieldValue);
            }

            return Collections.singletonList(makeLink(id, rel, argumentList.toArray()));
        }
    }

    private final static class ChainedLinkProducer implements LinkProducer<Object> {
        private final Collection<LinkProducer<Object>> wrappedCallbacks = new LinkedList<LinkProducer<Object>>();

        @SuppressWarnings("unchecked")
        public void append(LinkProducer<?> callback) {
            wrappedCallbacks.add((LinkProducer<Object>) callback);
        }

        @Override
        public Collection<HateoasLink> getLinks(Object entity) {
            Collection<HateoasLink> result = new LinkedList<HateoasLink>();
            for (LinkProducer<Object> callback : wrappedCallbacks) {
                result.addAll(callback.getLinks(entity));
            }

            return result;
        }
    }

    public final static class FixedLinkProducer implements LinkProducer<Object> {
        private Collection<HateoasLink> links;

        public FixedLinkProducer(HateoasLink link) {
            this(Collections.singleton(link));
        }

        public FixedLinkProducer(Collection<HateoasLink> links) {
            this.links = links;
        }

        @Override
        public Collection<HateoasLink> getLinks(Object entity) {
            return links;
        }
    }

}
