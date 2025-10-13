package ru.ssau.tk._shederu_._lab1_.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._shederu_._lab1_.exceptions.InconsistentFunctionsException;
import ru.ssau.tk._shederu_._lab1_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.Point;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._shederu_._lab1_.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk._shederu_._lab1_.functions.factory.TabulatedFunctionFactory;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionOperationServiceTest {
    private final double eRate = 1e-9;

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
    @Test
    void testConstructorWithFactory() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(factory);

        assertSame(factory, service.getFactory());
    }

    @Test
    void testDefaultConstructor() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        assertNotNull(service.getFactory());
        assertTrue(service.getFactory() instanceof ArrayTabulatedFunctionFactory);
    }

    @Test
    void testSetFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();
        TabulatedFunctionFactory newFactory = new LinkedListTabulatedFunctionFactory();

        service.setFactory(newFactory);

        assertSame(newFactory, service.getFactory());
    }

    @Test
    void testSetFactoryWithNull() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        assertThrows(IllegalArgumentException.class, () -> {
            service.setFactory(null);
        });
    }

    @Test
    void testConstructorWithNullFactory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TabulatedFunctionOperationService(null);
        });
    }

    @Test
    void testAdd() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {2.0, 3.0, 4.0};
        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {5.0, 6.0, 7.0};

        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction function2 = new ArrayTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.add(function1, function2);

        assertEquals(3, result.getCount());
        assertEquals(7.0, result.getY(0), eRate);
        assertEquals(9.0, result.getY(1), eRate);
        assertEquals(11.0, result.getY(2), eRate);
    }

    @Test
    void testAddWithLinkedListFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 2.0, 3.0};
        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {4.0, 5.0, 6.0};

        TabulatedFunction function1 = new LinkedListTabulatedFunction(xValues1, yValues1);
        TabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.add(function1, function2);


        assertTrue(result instanceof LinkedListTabulatedFunction);
        assertEquals(5.0, result.getY(0), eRate);
        assertEquals(7.0, result.getY(1), eRate);
        assertEquals(9.0, result.getY(2), eRate);
    }

    @Test
    void testAddWithDifferentXValues() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {2.0, 3.0, 4.0};
        double[] xValues2 = {1.0, 2.5, 3.0};
        double[] yValues2 = {5.0, 6.0, 7.0};

        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction function2 = new ArrayTabulatedFunction(xValues2, yValues2);

        assertThrows(InconsistentFunctionsException.class, () -> service.add(function1, function2));
    }

    @Test
    void testAddWithDifferentCount() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {2.0, 3.0, 4.0};
        double[] xValues2 = {1.0, 2.0};
        double[] yValues2 = {5.0, 6.0};

        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction function2 = new ArrayTabulatedFunction(xValues2, yValues2);

        assertThrows(InconsistentFunctionsException.class, () -> {service.add(function1, function2);});
    }

    @Test
    void testAddWithNullFunctions() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        TabulatedFunction function = new ArrayTabulatedFunction(new double[]{1.0, 2.0}, new double[]{1.0, 2.0});

        assertThrows(IllegalArgumentException.class, () -> {
            service.add(null, function);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            service.add(function, null);
        });
    }

    @Test
    void testSubtract() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {10.0, 20.0, 30.0};
        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {2.0, 5.0, 8.0};

        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction function2 = new ArrayTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.subtract(function1, function2);

        assertEquals(3, result.getCount());
        assertEquals(8.0, result.getY(0), eRate);
        assertEquals(15.0, result.getY(1), eRate);
        assertEquals(22.0, result.getY(2), eRate);
    }

    @Test
    void testSubtractWithLinkedListFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {8.0, 12.0, 18.0};
        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {2.0, 3.0, 6.0};

        TabulatedFunction function1 = new LinkedListTabulatedFunction(xValues1, yValues1);
        TabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.subtract(function1, function2);

        assertTrue(result instanceof LinkedListTabulatedFunction);
        assertEquals(6.0, result.getY(0), eRate);
        assertEquals(9.0, result.getY(1), eRate);
        assertEquals(12.0, result.getY(2), eRate);
    }

    @Test
    void testSubtractWithNegativeValues() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {5.0, 3.0, 1.0};
        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {2.0, 4.0, 6.0};

        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction function2 = new ArrayTabulatedFunction(xValues2, yValues2);


        TabulatedFunction result = service.subtract(function1, function2);

        assertEquals(3.0, result.getY(0), eRate);
        assertEquals(-1.0, result.getY(1), eRate);
        assertEquals(-5.0, result.getY(2), eRate);
    }

    @Test
    void testMixedFunctionTypes() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues1 = {2.0, 4.0, 6.0};
        double[] yValues2 = {1.0, 2.0, 3.0};

        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues1);
        TabulatedFunction linkedFunction = new LinkedListTabulatedFunction(xValues, yValues2);

        TabulatedFunction addResult = service.add(arrayFunction, linkedFunction);
        assertEquals(3.0, addResult.getY(0), eRate);
        assertEquals(6.0, addResult.getY(1), eRate);
        assertEquals(9.0, addResult.getY(2), eRate);

        TabulatedFunction subtractResult = service.subtract(arrayFunction, linkedFunction);
        assertEquals(1.0, subtractResult.getY(0), eRate);
        assertEquals(2.0, subtractResult.getY(1), eRate);
        assertEquals(3.0, subtractResult.getY(2), eRate);
    }
}

