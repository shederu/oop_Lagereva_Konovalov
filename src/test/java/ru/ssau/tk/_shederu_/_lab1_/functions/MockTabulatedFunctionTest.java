package ru.ssau.tk._shederu_._lab1_.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MockTabulatedFunctionTest{
    private final double delta = 0.001;

    @Test
    public void testInterpolate() {
        MockTabulatedFunction function1 = new MockTabulatedFunction(1, 2, 8, 3);
        MockTabulatedFunction function2 = new MockTabulatedFunction(-5, 7, -2, -1);

        assertEquals(5.5, function1.interpolate(1.5, function1.floorIndexOfX(1.5)), delta);
        assertEquals(-1.333, function2.interpolate(3, -5, 7, -2, -1), delta);
    }

    @Test
    public void testApply() {
        MockTabulatedFunction function1 = new MockTabulatedFunction(-38, 15, 23, 41);
        MockTabulatedFunction function2 = new MockTabulatedFunction(-6, -2, 1, 5);

        assertEquals(37.943, function1.apply(6), delta);
        assertEquals(-193.0, function2.apply(-200), delta);
        assertEquals(130.0, function2.apply(123), delta);
    }
}