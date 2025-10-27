package ru.ssau.tk._shederu_._lab1_.concurrent;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._shederu_._lab1_.functions.*;

import static org.junit.jupiter.api.Assertions.*;

class SynchronizedTabulatedFunctionTest {
    double delta = 1e-9;

    @Test
    void testGetCount() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(11, syncFunction.getCount());
    }

    @Test
    void testGetX() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(0.0, syncFunction.getX(0), delta);
        assertEquals(5.0, syncFunction.getX(5), delta);
        assertEquals(10.0, syncFunction.getX(10), delta);
    }

    @Test
    void testGetY() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(1.0, syncFunction.getY(0), delta);
        assertEquals(1.0, syncFunction.getY(5), delta);
    }

    @Test
    void testSetY() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        syncFunction.setY(0, 42.0);
        assertEquals(42.0, syncFunction.getY(0), delta);

        syncFunction.setY(5, 100.0);
        assertEquals(100.0, syncFunction.getY(5), delta);
    }

    @Test
    void testIndexOfX() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(0, syncFunction.indexOfX(0.0));
        assertEquals(5, syncFunction.indexOfX(5.0));
        assertEquals(10, syncFunction.indexOfX(10.0));
        assertEquals(-1, syncFunction.indexOfX(15.0));
    }

    @Test
    void testIndexOfY() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(0, syncFunction.indexOfY(1.0));

        syncFunction.setY(3, 5.0);
        assertEquals(3, syncFunction.indexOfY(5.0));
        assertEquals(-1, syncFunction.indexOfY(999.0));
    }

    @Test
    void testLeftBound() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(0.0, syncFunction.leftBound(), delta);
    }

    @Test
    void testRightBound() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(10.0, syncFunction.rightBound(), delta);
    }

    @Test
    void testIterator() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 2, 3);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        int count = 0;
        for (Point point : syncFunction) {
            assertEquals(point.x, syncFunction.getX(count), delta);
            assertEquals(point.y, syncFunction.getY(count), delta);
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void testDoSynchronously() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        String result = syncFunction.doSynchronously(func -> {
            for (int i = 0; i < func.getCount(); i++) {
                func.setY(i, func.getY(i) * 2);
            }
            return "Operation completed";
        });

        assertEquals("Operation completed", result);
        assertEquals(2.0, syncFunction.getY(0), delta);
        assertEquals(2.0, syncFunction.getY(5), delta);
    }

    @Test
    void testApply() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(x -> x * x, 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        assertEquals(0.0, syncFunction.apply(0.0), delta);
        assertEquals(25.0, syncFunction.apply(5.0), delta);
        assertEquals(100.0, syncFunction.apply(10.0), delta);

        double interpolated = syncFunction.apply(2.5);
        assertTrue(interpolated >= 4.0 && interpolated <= 9.0);
    }
}