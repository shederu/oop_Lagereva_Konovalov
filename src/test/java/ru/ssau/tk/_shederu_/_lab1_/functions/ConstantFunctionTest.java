package ru.ssau.tk._shederu_._lab1_.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConstantFunctionTest{
    private final ConstantFunction first = new ConstantFunction(5.0);
    private final ConstantFunction second = new ConstantFunction(-10.0);
    private final ConstantFunction third = new ConstantFunction(-15.3);

    @Test
    public void testApply() {
        assertEquals(5.0, first.apply(-3.4));
        assertEquals(-10.0, second.apply(23.6));
        assertEquals(-15.3, third.apply(-8.6));
    }

    @Test
    public void testGetconst() {
        assertEquals(5.0, first.getconst());
        assertEquals(-10.0, second.getconst());
        assertEquals(-15.3, third.getconst());
    }
}