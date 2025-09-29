package ru.ssau.tk._shederu_._lab1_.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ZeroFunctionTest{
    private ZeroFunction first = new ZeroFunction();
    private ZeroFunction second = new ZeroFunction();
    private ZeroFunction third = new ZeroFunction();

    @Test
    public void testApply() {
        assertEquals(0.0, first.apply(-3.4));
        assertEquals(0.0, second.apply(23.6));
        assertEquals(0.0, third.apply(-8.6));
    }

    @Test
    public void testGetconst() {
        assertEquals(0.0, first.getconst());
        assertEquals(0.0, second.getconst());
        assertEquals(0.0, third.getconst());
    }
}