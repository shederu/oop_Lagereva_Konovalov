package ru.ssau.tk._shederu_._lab1_.functions;

import junit.framework.TestCase;

public class RungeCuteTest extends TestCase {
    private RungeCute testt;
    private MathFunctions derivative = x -> java.lang.Math.sin(x) + java.lang.Math.cos(x);

    public void setUp() throws Exception {
        super.setUp();
        testt = new RungeCute(derivative, 0.0, 0.0, 0.001);
    }

    public void tearDown() throws Exception {
        testt = null;
        derivative = null;
        super.tearDown();
    }

    public void testApply() {
        assertEquals(0.0, testt.apply(0.0));
        assertEquals(0.634, testt.apply(java.lang.Math.PI/6));
        assertEquals(2.0, testt.apply(java.lang.Math.PI/2));
        assertEquals(2.0, testt.apply(java.lang.Math.PI));
        assertEquals(0.0, testt.apply(3*java.lang.Math.PI/2));
    }
}