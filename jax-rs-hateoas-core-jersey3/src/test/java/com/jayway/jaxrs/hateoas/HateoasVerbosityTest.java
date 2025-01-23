package com.jayway.jaxrs.hateoas;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Mattias Hellborg Arthursson
 */
public class HateoasVerbosityTest {

    @Test
    public void verifyValueOfWithSingleOption() {
        HateoasVerbosity verbosity = HateoasVerbosity.valueOf("REL");
        HateoasOption[] options = verbosity.getOptions();
        assertEquals(1, options.length);
        assertEquals(HateoasOption.REL, options[0]);
    }

    @Test
    public void verifyValueOfWithMultipleCommaSeparatedOptions() {
        HateoasVerbosity verbosity = HateoasVerbosity.valueOf("REL, METHOD, CONSUMES");
        HateoasOption[] options = verbosity.getOptions();
        assertEquals(3, options.length);
        assertEquals(HateoasOption.REL, options[0]);
        assertEquals(HateoasOption.METHOD, options[1]);
        assertEquals(HateoasOption.CONSUMES, options[2]);
    }

    @Test
    public void verifyValueOfWithMultipleSemicolonSeparatesOptions() {
        HateoasVerbosity verbosity = HateoasVerbosity.valueOf("REL;METHOD;CONSUMES");
        HateoasOption[] options = verbosity.getOptions();
        assertEquals(3, options.length);
        assertEquals(HateoasOption.REL, options[0]);
        assertEquals(HateoasOption.METHOD, options[1]);
        assertEquals(HateoasOption.CONSUMES, options[2]);
    }

    @Test
    public void verifyValueOfWithEmptyString() {
        HateoasVerbosity verbosity = HateoasVerbosity.valueOf("");
        assertEquals(HateoasVerbosity.MAXIMUM, verbosity);
    }
}
