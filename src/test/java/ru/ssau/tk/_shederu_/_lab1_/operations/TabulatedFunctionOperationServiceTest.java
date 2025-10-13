package ru.ssau.tk._shederu_._lab1_.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._shederu_._lab1_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.Point;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionOperationServiceTest {

    @Test
    void asPointsCorrectArray() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {3.0, 4.0, 6.0, 8.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues,yValues);
        Point[] result = TabulatedFunctionOperationService.asPoints(function);
        assertEquals(4, result.length);
        assertEquals(new Point(1.0, 3.0).x, result[0].x);
        assertEquals(new Point(1.0, 3.0).y, result[0].y);
        assertEquals(new Point(2.0, 4.0).x, result[1].x);
        assertEquals(new Point(2.0, 4.0).y, result[1].y);
        assertEquals(new Point(3.0, 6.0).x, result[2].x);
        assertEquals(new Point(3.0, 6.0).y, result[2].y);
        assertEquals(new Point(4.0, 8.0).x, result[3].x);
        assertEquals(new Point(4.0, 8.0).y, result[3].y);

        assertThrows(NullPointerException.class, ()-> { TabulatedFunctionOperationService.asPoints(null);});
    }
}