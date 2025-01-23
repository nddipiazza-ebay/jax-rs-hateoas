package com.jayway.jaxrs.hateoas;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marking a method with @Linkable enables it to be linked from elsewhere in the application. Note that the id
 * <b>must</b> be unique in an application.
 *
 * @author Mattias Hellborg Arthursson
 * @author Kalle Stenflo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Linkable {
	public final static class NoTemplate {

	}

    /**
     * Identifier of this linkable method.
     */
	String value();

    /**
     * The class to use for generating a template in links.
     */
	Class<?> templateClass() default NoTemplate.class;

    /**
     * Label of this link.
     */
	String label() default "";

    /**
     * Description of this link.
     */
	String description() default "";
}
