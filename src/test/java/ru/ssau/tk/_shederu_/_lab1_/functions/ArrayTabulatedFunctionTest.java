package ru.ssau.tk._shederu_._lab1_.functions;

import junit.framework.TestCase;

public class ArrayTabulatedFunctionTest extends TestCase {

    public void testConstructorAndMethods(){
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 2.0, 3.0, 4.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(4,function.getCount());
        assertEquals(3.0, function.getX(2));
        assertEquals(4.0, function.getY(3));

        function.setY(1, 8.0);

        assertEquals(8.0,function.getY(1));
        assertEquals(1,function.indexOfY(8.0));

        assertEquals(1, function.floorIndexOfX(2.5));

        assertEquals(3, function.indexOfX(4.0));
        assertEquals(2, function.indexOfY(3.0));
    }

    public void testFunctionConstructor(){
        MathFunctions function = new SqrFunction();
        ArrayTabulatedFunction tabFunction = new ArrayTabulatedFunction(function, 3.0, 6.0, 4);

        assertEquals(4, tabFunction.getCount());
        assertEquals(3.0, tabFunction.getX(0));
        assertEquals(9.0, tabFunction.getY(0));
        assertEquals(4.0, tabFunction.getX(1));
        assertEquals(16.0, tabFunction.getY(1));
        assertEquals(6.0, tabFunction.getX(3));
        assertEquals(36.0, tabFunction.getY(3));

        ArrayTabulatedFunction secondTabFunction = new ArrayTabulatedFunction(function, 5.0, 0.0, 3);

        assertEquals(0.0, secondTabFunction.getX(0));
        assertEquals(5.0, secondTabFunction.getX(2));

        ArrayTabulatedFunction equalBounds = new ArrayTabulatedFunction(function, 3.0, 3.0, 4);

        assertEquals(3.0, equalBounds.leftBound());
        assertEquals(3.0, equalBounds.rightBound());
        assertEquals(9.0, equalBounds.getY(1));
        assertEquals(9.0, equalBounds.getY(3));
    }

    public void testExtrapolateAndInterpolate(){
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {4.0, 6.0, 8.0, 10.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(-6.0, function.apply(-5.0));
        assertEquals(14.0, function.apply(5.0));
        assertEquals(7.0, function.apply(1.5));
    }
}