package ru.ssau.tk._shederu_._lab1_.functions;

import junit.framework.TestCase;

public class SqrFunctionTest extends TestCase {
    private SqrFunction function;

    public void setUp() throws Exception {
        super.setUp();
        function = new SqrFunction();
    }

    public void tearDown() throws Exception {
        function = null;
        super.tearDown();
    }

    public void testApply() {
        assertEquals("Квадрат нуля", 0.0, function.apply(0.0));
        assertEquals("Квадрат целого числа", 4.0, function.apply(2.0));
        assertEquals("Квадрат большого положительного целого числа", 100000000.0, function.apply(10000.0));
        assertEquals("Квадрат отрицательного целого числа", 25.0, function.apply(-5.0) );
        assertEquals("Квадрат большого отрицательногоцелого  числа", 100000000.0, function.apply(-10000.0));

        assertEquals("Квадрат дробного числа", 0.25, function.apply(0.5));
        assertEquals("Квадрат дробного отрицательного числа", 0.25, function.apply(-0.5));
    }
}