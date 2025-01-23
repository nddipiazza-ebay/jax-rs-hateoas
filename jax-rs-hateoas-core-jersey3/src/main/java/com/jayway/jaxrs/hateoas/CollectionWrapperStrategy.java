package com.jayway.jaxrs.hateoas;

import java.util.Collection;

public interface CollectionWrapperStrategy {
    Object wrapRootCollection(Collection<Object> rootCollection);
    String rowsFieldName();
}
