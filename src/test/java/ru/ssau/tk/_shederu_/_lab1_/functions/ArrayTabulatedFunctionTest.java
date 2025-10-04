package ru.ssau.tk._shederu_._lab1_.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._shederu_._lab1_.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk._shederu_._lab1_.exceptions.DifferentLengthOfArraysException;
import ru.ssau.tk._shederu_._lab1_.exceptions.InterpolationException;

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

        assertEquals(5.6, function.apply(0.8));
        assertEquals(4.6, function.apply(0.3));
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
    public void testInsert() {
        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {2.0, 6.0, 10.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Вставка в начало
        function.insert(0.0, 1.0);
        assertEquals(4, function.getCount());
        assertEquals(0.0, function.getX(0));
        assertEquals(1.0, function.getY(0));

        // Вставка в середину
        function.insert(2.0, 4.0);
        assertEquals(5, function.getCount());
        assertEquals(2.0, function.getX(2));
        assertEquals(4.0, function.getY(2));

        // Вставка в конец
        function.insert(6.0, 12.0);
        assertEquals(6, function.getCount());
        assertEquals(6.0, function.getX(5));
        assertEquals(12.0, function.getY(5));

        // Обновление существующего значения
        function.insert(2.0, 8.0);
        assertEquals(6, function.getCount());
        assertEquals(8.0, function.getY(2));
    }

    @Test
    public void checkSortedTest(){
        double[] yValues = {7.0, 2.0, 1.0};
        double[] xValues = {3.0, 4.0, -2.0};

        assertThrows(ArrayIsNotSortedException.class, () -> new ArrayTabulatedFunction(xValues, yValues));

        double[] xValues2 = {-10.0, 13.0, -1.0};
        assertThrows(ArrayIsNotSortedException.class, () -> new ArrayTabulatedFunction(xValues2, yValues));
    }

    @Test
    public void checkLengths(){
        double[] xValues = {9.0, 12.0, 14.0, 25.0};
        double[] yValues = {-4.0, 2.0, 3.0};

        assertThrows(DifferentLengthOfArraysException.class, () -> new ArrayTabulatedFunction(xValues, yValues));

        double[] xValues2 = {10, 11, 12, 13, 14, 15};
        assertThrows(DifferentLengthOfArraysException.class, ()-> new ArrayTabulatedFunction(xValues2, yValues));

        double[] yValues2 = {-3, 2, 5, 7};
        assertThrows(DifferentLengthOfArraysException.class, () -> new ArrayTabulatedFunction(xValues2, yValues2));
    }

    @Test
    public void testInterpolateException(){
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {4.0, 6.0, 8.0, 10.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertThrows(InterpolationException.class, () -> function.apply(-20.0));
        assertThrows(InterpolationException.class, () -> function.apply(5.0));
        assertThrows(InterpolationException.class, () -> function.apply(10.0));
    }
}