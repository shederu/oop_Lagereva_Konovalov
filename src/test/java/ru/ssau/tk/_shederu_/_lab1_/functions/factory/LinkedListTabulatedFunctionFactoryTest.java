package ru.ssau.tk._shederu_._lab1_.functions.factory;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._shederu_._lab1_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionFactoryTest {

    private final LinkedListTabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
    private static final double eRate = 1e-9;
    

    @Test
    void testCreatedFunctionContainsCorrectValues() {
        double[] xData = {1.0, 3.0, 5.0};
        double[] yData = {2.0, 6.0, 10.0};

        TabulatedFunction function = factory.create(xData, yData);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0), eRate);
        assertEquals(2.0, function.getY(0), eRate);
        assertEquals(5.0, function.getX(2), eRate);
        assertEquals(10.0, function.getY(2), eRate);
    }

    @Test
    void testTwoPointFunctionCreation() {
        double[] x = {5.0, 10.0};
        double[] y = {25.0, 100.0};

        TabulatedFunction func = factory.create(x, y);

        assertEquals(2, func.getCount());
        assertEquals(5.0, func.leftBound(), eRate);
        assertEquals(10.0, func.rightBound(), eRate);
    }

    @Test
    void testFunctionWithMixedSigns() {
        double[] x = {-4.0, -2.0, 0.0, 2.0, 4.0};
        double[] y = {16.0, 4.0, 0.0, 4.0, 16.0};

        TabulatedFunction func = factory.create(x, y);

        assertEquals(-4.0, func.getX(0), eRate);
        assertEquals(16.0, func.getY(0), eRate);
        assertEquals(0.0, func.getX(2), eRate);
        assertEquals(4.0, func.getX(4), eRate);
    }

    @Test
    void testDataIsolation() {
        double[] sourceX = {2.0, 4.0, 6.0};
        double[] sourceY = {4.0, 16.0, 36.0};

        TabulatedFunction func = factory.create(sourceX, sourceY);

        sourceX[0] = 999.0;
        sourceY[1] = 888.0;

        assertEquals(2.0, func.getX(0), eRate);
        assertEquals(16.0, func.getY(1), eRate);
    }

    @Test
    void testYValueModification() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {1.0, 2.0, 3.0};

        TabulatedFunction func = factory.create(x, y);

        func.setY(1, 5.0);

        assertEquals(5.0, func.getY(1), eRate);
        assertEquals(5.0, func.apply(2.0), eRate);
    }

    @Test
    void testFunctionWithRepeatedValues() {
        double[] x = {1.0, 2.0, 3.0, 4.0};
        double[] y = {8.0, 8.0, 8.0, 8.0};

        TabulatedFunction func = factory.create(x, y);

        for (int i = 0; i < func.getCount(); i++) {
            assertEquals(8.0, func.getY(i), eRate);
        }
    }

    @Test
    void testIteratorFunctionality() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {1.0, 4.0, 9.0};

        TabulatedFunction func = factory.create(x, y);

        int pointsCount = 0;
        for (var point : func) {
            assertNotNull(point);
            pointsCount++;
        }

        assertEquals(3, pointsCount);
    }
}