package ru.ssau.tk._shederu_._lab1_.functions;

import junit.framework.TestCase;

public class UnitFunctionTest extends TestCase {
    private UnitFunction first = new UnitFunction();
    private UnitFunction second = new UnitFunction();
    private UnitFunction third = new UnitFunction();

    public void testApply() {
        assertEquals(1.0, first.apply(-3.4));
        assertEquals(1.0, second.apply(23.6));
        assertEquals(1.0, third.apply(-8.6));
    }

    public void testGetconst() {
        assertEquals(1.0, first.getconst());
        assertEquals(1.0, second.getconst());
        assertEquals(1.0, third.getconst());
    }
}