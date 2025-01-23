package com.jayway.jaxrs.hateoas.support;

import com.jayway.jaxrs.hateoas.CollectionWrapperStrategy;

import java.util.Collection;

/**
 * @author Mattias Hellborg Arthursson
 */
public class DefaultCollectionWrapperStrategy implements CollectionWrapperStrategy {
    @Override
    public Object wrapRootCollection(Collection<Object> rootCollection) {
        return new DefaultCollectionWrapper<Object>(rootCollection);
    }

    @Override
    public String rowsFieldName() {
        return DefaultCollectionWrapper.ROWS_FIELD_NAME;
    }
}
