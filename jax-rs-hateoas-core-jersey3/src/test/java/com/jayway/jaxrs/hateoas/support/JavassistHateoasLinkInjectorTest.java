package com.jayway.jaxrs.hateoas.support;

import com.google.common.collect.Iterables;
import com.jayway.jaxrs.hateoas.HateoasInjectException;
import com.jayway.jaxrs.hateoas.HateoasLink;
import com.jayway.jaxrs.hateoas.HateoasVerbosity;
import com.jayway.jaxrs.hateoas.core.HateoasResponseBuilderImpl.FixedLinkProducer;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 */
public class JavassistHateoasLinkInjectorTest {
    private final static Map<String, Object> EXPECTED_MAP = new HashMap<String, Object>();
    private HateoasLink linkMock;
    private JavassistHateoasLinkInjector tested;
    private FixedLinkProducer linkProducer;

    @Before
    public void prepareTestedInstance() {
        tested = new JavassistHateoasLinkInjector();
        linkMock = mock(HateoasLink.class);
        linkProducer = new FixedLinkProducer(linkMock);
        when(linkMock.toMap(HateoasVerbosity.MINIMUM)).thenReturn(EXPECTED_MAP);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void linksFieldIsInjectedAutomatically() {
        DummyEntity dummyEntity = new DummyEntity();
        dummyEntity.setId("someId");

        DummyEntity returnedEntity = (DummyEntity) tested.injectLinks(
                dummyEntity, linkProducer, HateoasVerbosity.MINIMUM);

        assertNotSame(dummyEntity, returnedEntity);
        assertEquals("someId", returnedEntity.getId());

        Collection<Map<String, Object>> links = (Collection<Map<String, Object>>) ReflectionUtils
                .getFieldValue(returnedEntity, "links");
        assertSame(EXPECTED_MAP, Iterables.getOnlyElement(links));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = HateoasInjectException.class)
    public void defaultConstructorIsRequired() {
        DummyEntityNoDefaultConstructor dummyEntity = new DummyEntityNoDefaultConstructor("someId");

        tested.injectLinks(dummyEntity, linkProducer, HateoasVerbosity.MINIMUM);


    }

    @SuppressWarnings("unchecked")
    @Test
    public void linksFieldIsInjectedAutomaticallyInSublass() {
        DummySubClass dummyEntity = new DummySubClass("someId", 1L);

        DummySubClass returnedEntity = (DummySubClass) tested.injectLinks(
                dummyEntity, linkProducer, HateoasVerbosity.MINIMUM);

        assertNotSame(dummyEntity, returnedEntity);
        assertEquals("someId", returnedEntity.getId());
        assertEquals(1L, returnedEntity.getTime());

        Collection<Map<String, Object>> links = (Collection<Map<String, Object>>) ReflectionUtils
                .getFieldValue(returnedEntity, "links");
        assertSame(EXPECTED_MAP, Iterables.getOnlyElement(links));
    }


    public static class DummyEntityNoDefaultConstructor {
        private String id;


        public DummyEntityNoDefaultConstructor(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class DummyEntity {
        private String id;

        public DummyEntity() {
        }

        public DummyEntity(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class DummySubClass extends DummyEntity {

        private long time;

        public DummySubClass() {
        }

        public DummySubClass(String id, long time) {
            super(id);
            this.time = time;
        }

        public long getTime() {
            return time;
        }
    }
}
