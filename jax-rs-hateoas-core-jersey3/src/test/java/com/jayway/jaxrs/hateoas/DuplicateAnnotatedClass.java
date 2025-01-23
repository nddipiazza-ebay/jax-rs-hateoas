package com.jayway.jaxrs.hateoas;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/dummy/")
public class DuplicateAnnotatedClass {

	@GET
	@Linkable(value = "test.dummy.get")
	public Response get() {
		return null;
	}
}
