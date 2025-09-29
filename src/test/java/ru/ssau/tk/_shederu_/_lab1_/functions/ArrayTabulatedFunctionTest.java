package ru.ssau.tk._shederu_._lab1_.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArrayTabulatedFunctionTest{
    private final double eRate = 1e-10;

    @Test
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

    @Test
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

    @Test
    public void testExtrapolateAndInterpolate(){
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {4.0, 6.0, 8.0, 10.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(-6.0, function.apply(-5.0));
        assertEquals(14.0, function.apply(5.0));
        assertEquals(7.0, function.apply(1.5));
    }

    @Test
    public void testRemove(){
        double[] xValues = {-16.0, -2.0, 1.0, 3.0, 6.0};
        double[] yValues = {3.0, 5.0, -2.0, 4.0, 8.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(-2.0, function.getX(1));
        assertEquals(5.0, function.getY(1));
        function.remove(1);
        assertEquals(1.0, function.getX(1));
        assertEquals(-2.0, function.getY(1));

        assertEquals(3.0, function.getX(2));
        assertEquals(4.0, function.getY(2));
        function.remove(2);
        assertEquals(6.0, function.getX(2));
        assertEquals(8.0, function.getY(2));
    }

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
}