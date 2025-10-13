package ru.ssau.tk._shederu_._lab1_.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._shederu_._lab1_.functions.MathFunctions;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.factory.ArrayTabulatedFunctionFactory;

import static org.junit.jupiter.api.Assertions.*;

class DifferentialOperatorTest {

    @Test
    void testInterfaceImplementation() {
        DifferentialOperator<TabulatedFunction> operator = new TabulatedDifferentialOperator();

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        TabulatedFunction function = new ArrayTabulatedFunctionFactory().create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertNotNull(derivative);
        assertTrue(derivative instanceof MathFunctions);
    }
}