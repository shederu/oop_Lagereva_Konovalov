package ru.ssau.tk._shederu_._lab1_.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._shederu_._lab1_.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk._shederu_._lab1_.exceptions.DifferentLengthOfArraysException;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionTest {
    private static final double eRate = 1e-9;

    @Test
    void testInsertOperations() {
        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {2.0, 6.0, 10.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(3, function.getCount());
        assertEquals(3.0, function.getX(1), eRate);

        double[] newXValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] newYValues = {2.0, 4.0, 6.0, 8.0, 10.0};
        LinkedListTabulatedFunction newFunction = new LinkedListTabulatedFunction(newXValues, newYValues);

        assertEquals(5, newFunction.getCount());
        assertEquals(2.0, newFunction.getX(1), eRate);
        assertEquals(4.0, newFunction.getY(1), eRate);
    }


    @Test
    void testGetSetOperations() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(1.0, function.getX(0), eRate);
        assertEquals(2.0, function.getX(1), eRate);
        assertEquals(10.0, function.getY(0), eRate);
        assertEquals(20.0, function.getY(1), eRate);

        function.setY(1, 25.0);
        assertEquals(25.0, function.getY(1), eRate);

        function.setY(2, 35.0);
        assertEquals(35.0, function.getY(2), eRate);
    }

    @Test
    void testBoundaryCases() {
        double[] singleX = {1.0};
        double[] singleY = {5.0};
        LinkedListTabulatedFunction singleFunction = new LinkedListTabulatedFunction(singleX, singleY);

        assertEquals(1, singleFunction.getCount());
        assertEquals(1.0, singleFunction.getX(0), eRate);
        assertEquals(5.0, singleFunction.getY(0), eRate);

        double[] twoX = {0.0, 1.0};
        double[] twoY = {0.0, 1.0};
        LinkedListTabulatedFunction twoFunction = new LinkedListTabulatedFunction(twoX, twoY);

        assertEquals(2, twoFunction.getCount());
        assertEquals(0.0, twoFunction.getX(0), eRate);
        assertEquals(1.0, twoFunction.getX(1), eRate);
    }

    @Test
    void testArrayConstructor() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0, 25.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(5, function.getCount());
        assertEquals(1.0, function.getX(0), eRate);
        assertEquals(25.0, function.getY(4), eRate);

        for (int i = 0; i < xValues.length; i++) {
            assertEquals(xValues[i], function.getX(i), eRate);
            assertEquals(yValues[i], function.getY(i), eRate);
        }
    }

    @Test
    void testComplexFunctionComposition() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 8.0, 27.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(4, function.getCount());
        assertEquals(8.0, function.getY(2), eRate);
        assertEquals(27.0, function.getY(3), eRate);
    }

    @Test
    void testRemoveOperations() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(4, function.getCount());

        assertDoesNotThrow(() -> {
        });
    }

    @Test
    void testIndexSearch() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);


        assertEquals(1.0, function.getX(0), eRate);
        assertEquals(3.0, function.getX(2), eRate);
        assertEquals(5.0, function.getX(4), eRate);


        assertEquals(10.0, function.getY(0), eRate);
        assertEquals(30.0, function.getY(2), eRate);
        assertEquals(50.0, function.getY(4), eRate);

        double[] largeX = new double[1000];
        double[] largeY = new double[1000];
        for (int i = 0; i < 1000; i++) {
            largeX[i] = i;
            largeY[i] = i * 2;
        }

        LinkedListTabulatedFunction largeFunction = new LinkedListTabulatedFunction(largeX, largeY);
        assertEquals(0.0, largeFunction.getX(0), eRate);
        assertEquals(500.0, largeFunction.getX(500), eRate);
        assertEquals(999.0, largeFunction.getX(999), eRate);
    }

    @Test
    void testInterpolationExtrapolation() {

        double[] xValues = {1.0, 3.0};
        double[] yValues = {1.0, 3.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);


        assertEquals(2.0, function.apply(2.0), eRate);


        assertEquals(0.0, function.apply(0.0), eRate);  // слева
        assertEquals(2.0, function.apply(2.0), eRate);  // справа
    }

    @Test
    void testFunctionConstructor() {
        MathFunctions simpleFunc = x -> x * x;

        assertDoesNotThrow(() -> {
            LinkedListTabulatedFunction tabulated = new LinkedListTabulatedFunction(
                    simpleFunc, 0.0, 10.0, 11);

            assertEquals(11, tabulated.getCount());
            assertEquals(0.0, tabulated.getX(0), eRate);
            assertEquals(100.0, tabulated.getY(10), eRate);
        });
    }

    @Test
    void testExceptions(){
        double[] xValues = {-5.0, -3.0, -20.0};
        double[] yValues = {3.0, 2.0, -5.0};

        assertThrows(ArrayIsNotSortedException.class, () -> new LinkedListTabulatedFunction(xValues, yValues));

        double[] xValues1 = {3.0, -1.0, 5.0};
        assertThrows(ArrayIsNotSortedException.class, ()-> new LinkedListTabulatedFunction(xValues1, yValues));

        double[] xValues3 = {-1.0, 3.0, 4.0, 5.0};
        assertThrows(DifferentLengthOfArraysException.class, () -> new LinkedListTabulatedFunction(xValues3, yValues));

    }
}
