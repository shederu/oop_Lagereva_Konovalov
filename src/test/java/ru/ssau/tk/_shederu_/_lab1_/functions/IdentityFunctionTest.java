package ru.ssau.tk._shederu_._lab1_.functions;

import junit.framework.TestCase;

public class IdentityFunctionTest extends TestCase {
    private IdentityFunction test;

    public void setUp() throws Exception {
        super.setUp();
        test = new IdentityFunction();
    }

    public void tearDown() throws Exception {
        test = null;
        super.tearDown();
    }

    public void testApply() {
        assertEquals(1.0, test.apply(1.0));
        assertEquals(9007199254740992.0, test.apply(9007199254740992.0));
        assertEquals( 4.9E-324, test.apply(4.9E-324));

    }
}
