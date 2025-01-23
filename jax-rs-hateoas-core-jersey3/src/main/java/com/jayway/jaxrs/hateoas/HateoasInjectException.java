package com.jayway.jaxrs.hateoas;

/**
 * Exception thrown when something goes wrong injecting links into an entity.
 *
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 */
public class HateoasInjectException extends RuntimeException {

	private static final long serialVersionUID = -7586666921228435121L;

    public HateoasInjectException(String message) {
        super(message);
    }

    public HateoasInjectException(Exception e) {
		super(e);
	}

}
