package com.jayway.jaxrs.hateoas;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dummy")
public class DummyAnnotatedClass {

	@PUT
	@Linkable(value = "test.dummy.fully.documented", description = "test description", label = "test label", templateClass = DummyDto.class)
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.TEXT_HTML, MediaType.TEXT_PLAIN })
	public Response fullyDocumented(DummyDto input) {
		return null;
	}

	@GET
	@Linkable(value = "test.dummy.get")
	public Response get() {
		return null;
	}

	@GET
	@Linkable(value = "test.dummy.get.subpath")
	@Path("/subpath")
	public Response getSubpath() {
		return null;
	}

	@POST
	@Linkable(value = "test.dummy.post")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(DummyDto input) {
		return null;
	}

	@DELETE
	@Linkable(value = "test.dummy.delete")
	public Response delete(DummyDto input) {
		return null;
	}
}
