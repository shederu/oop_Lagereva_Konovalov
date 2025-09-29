package ru.ssau.tk._shederu_._lab1_.functions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class SqrFunctionTest {
    private SqrFunction function;

    @BeforeEach
    void setUp() {
        function = new SqrFunction();
    }

    @AfterEach
    void tearDown() {
        function = null;
    }

    @Test
    void testApply() {
        assertEquals(0.0, function.apply(0.0), 1e-9, "Квадрат нуля");
        assertEquals(4.0, function.apply(2.0), 1e-9, "Квадрат целого числа");
        assertEquals(100000000.0, function.apply(10000.0), 1e-9, "Квадрат большого положительного целого числа");
        assertEquals(25.0, function.apply(-5.0), 1e-9, "Квадрат отрицательного целого числа");
        assertEquals(100000000.0, function.apply(-10000.0), 1e-9, "Квадрат большого отрицательного целого числа");
        assertEquals(0.25, function.apply(0.5), 1e-9, "Квадрат дробного числа");
        assertEquals(0.25, function.apply(-0.5), 1e-9, "Квадрат дробного отрицательного числа");
    }
}