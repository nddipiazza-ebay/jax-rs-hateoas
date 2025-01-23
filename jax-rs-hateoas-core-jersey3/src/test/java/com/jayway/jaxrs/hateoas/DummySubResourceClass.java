package com.jayway.jaxrs.hateoas;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/19/12
 * Time: 2:26 PM
 */
public class DummySubResourceClass {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Linkable("dummy.sub.resource.get")
    public Response get() {
        return Response.ok("hello from dummy sub").type(MediaType.TEXT_PLAIN).build();
    }
}
