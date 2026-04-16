package com.semmtech;


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


public class UtilTest {
    private final String nullString = null;
    private final String emptyString = "";
    private final String filledString = "This is a String";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public final void testIsNullOrEmpty() {
        Assert.assertTrue(Util.isNullOrEmpty(nullString));
        Assert.assertTrue(Util.isNullOrEmpty(emptyString));
        Assert.assertFalse(Util.isNullOrEmpty(filledString));
    }

    @SuppressWarnings("static-method")
    @Test
    public final void testFirstToUpper() {
        Assert.assertTrue(Util.firstToUpper("lower").equals("Lower"));
        Assert.assertTrue(Util.firstToUpper("UPPER").equals("UPPER"));
        Assert.assertTrue(Util.firstToUpper("").equals(""));
    }

    @SuppressWarnings("static-method")
    @Test
    public final void testFirstToLower() {
        Assert.assertTrue(Util.firstToLower("lower").equals("lower"));
        Assert.assertTrue(Util.firstToLower("UPPER").equals("uPPER"));
        Assert.assertTrue(Util.firstToLower("").equals(""));
    }

}
