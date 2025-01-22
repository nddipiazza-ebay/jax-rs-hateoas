package com.jayway.jaxrs.hateoas.core.jersey;

import com.jayway.jaxrs.hateoas.HateoasViewFactory;
import org.glassfish.jersey.server.mvc.Viewable;

import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/24/12
 * Time: 5:17 PM
 */
public class JerseyHateoasViewFactory implements HateoasViewFactory {
    @Override
    public Object createView(String template, Object model) {
        return new Viewable(template, Collections.singletonMap("model", model));
    }
}
