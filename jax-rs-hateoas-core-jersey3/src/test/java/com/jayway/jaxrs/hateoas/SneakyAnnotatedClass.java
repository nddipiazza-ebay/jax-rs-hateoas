package com.jayway.jaxrs.hateoas;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/dummy/")
public class SneakyAnnotatedClass {

	@GET
	@Linkable(value = "test.dummy.sneaky.get")
	public Response get() {
		return null;
	}

	@GET
	@Linkable(value = "test.dummy.sneaky.subpath")
	@Path("/subpath")
	public Response getSubpath() {
		return null;
	}

}
