package ru.ssau.tk._shederu_._lab1_.functions;

import junit.framework.TestCase;

public class ConstantFunctionTest extends TestCase {
    private ConstantFunction first = new ConstantFunction(5.0);
    private ConstantFunction second = new ConstantFunction(-10.0);
    private ConstantFunction third = new ConstantFunction(-15.3);

    public void testApply() {
        assertEquals(5.0, first.apply(-3.4));
        assertEquals(-10.0, second.apply(23.6));
        assertEquals(-15.3, third.apply(-8.6));
    }

    public void testGetconst() {
        assertEquals(5.0, first.getconst());
        assertEquals(-10.0, second.getconst());
        assertEquals(-15.3, third.getconst());
    }
}