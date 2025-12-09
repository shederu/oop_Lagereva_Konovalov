package ru.ssau.tk._shederu_._lab1_.functions.factory;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._shederu_._lab1_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionFactoryTest {

    private final ArrayTabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
    private static final double eRate = 1e-9;

    @Test
    void testCreatedFunctionHasCorrectData() {
        double[] xValues = {0.5, 1.5, 2.5};
        double[] yValues = {1.0, 3.0, 5.0};

        TabulatedFunction function = factory.create(xValues, yValues);

        assertEquals(3, function.getCount());
        assertEquals(0.5, function.getX(0), eRate);
        assertEquals(1.0, function.getY(0), eRate);
        assertEquals(2.5, function.getX(2), eRate);
        assertEquals(5.0, function.getY(2), eRate);
    }

    @Test
    void testFunctionWithTwoPoints() {
        double[] x = {2.0, 8.0};
        double[] y = {4.0, 64.0};

        TabulatedFunction func = factory.create(x, y);

        assertEquals(2, func.getCount());
        assertEquals(2.0, func.leftBound(), eRate);
        assertEquals(8.0, func.rightBound(), eRate);
    }

    @Test
    void testFunctionWithNegativeValues() {
        double[] x = {-3.0, -2.0, -1.0};
        double[] y = {9.0, 4.0, 1.0};

        TabulatedFunction func = factory.create(x, y);

        assertEquals(-3.0, func.getX(0), eRate);
        assertEquals(9.0, func.getY(0), eRate);
        assertEquals(-1.0, func.getX(2), eRate);
        assertEquals(1.0, func.getY(2), eRate);
    }

    @Test
    void testFunctionIndependenceFromSourceArrays() {
        double[] originalX = {1.0, 2.0, 3.0};
        double[] originalY = {1.0, 4.0, 9.0};

        TabulatedFunction func = factory.create(originalX, originalY);

        originalX[0] = 100.0;
        originalY[1] = 200.0;

        assertEquals(1.0, func.getX(0), eRate);
        assertEquals(4.0, func.getY(1), eRate);
    }

    @Test
    void testFunctionAllowsYModification() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {10.0, 20.0, 30.0};

        TabulatedFunction func = factory.create(x, y);

        func.setY(1, 25.0);

        assertEquals(25.0, func.getY(1), eRate);
        assertEquals(25.0, func.apply(2.0), eRate);
    }

    @Test
    void testFunctionInterpolatesCorrectly() {
        double[] x = {0.0, 2.0, 4.0};
        double[] y = {0.0, 4.0, 16.0};

        TabulatedFunction func = factory.create(x, y);

        assertEquals(2.0, func.apply(1.0), eRate);
        assertEquals(10.0, func.apply(3.0), eRate);
    }

    @Test
    void testFunctionWithManyPoints() {
        double[] x = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] y = {1.0, 4.0, 9.0, 16.0, 25.0};

        TabulatedFunction func = factory.create(x, y);

        assertEquals(5, func.getCount());
        assertEquals(1.0, func.getX(0), eRate);
        assertEquals(25.0, func.getY(4), eRate);
    }
}