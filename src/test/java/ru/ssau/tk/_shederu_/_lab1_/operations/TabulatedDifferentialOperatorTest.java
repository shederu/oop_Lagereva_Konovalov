package ru.ssau.tk._shederu_._lab1_.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._shederu_._lab1_.concurrent.SynchronizedTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.*;
import ru.ssau.tk._shederu_._lab1_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._shederu_._lab1_.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk._shederu_._lab1_.functions.factory.TabulatedFunctionFactory;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedDifferentialOperatorTest {
    private final double eRate = 1e-9;

    @Test
    void testDeriveWithRightDifference() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {3.0, 5.0, 7.0, 9.0};

        TabulatedFunction linearFunction = operator.getFactory().create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(linearFunction);

        assertEquals(4, derivative.getCount());
        assertEquals(2.0, derivative.getY(0), eRate);
        assertEquals(2.0, derivative.getY(1), eRate);
        assertEquals(2.0, derivative.getY(2), eRate);
        assertEquals(2.0, derivative.getY(3), eRate);
    }

    @Test
    void testDeriveQuadraticFunction() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};

        TabulatedFunction quadraticFunction = operator.getFactory().create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(quadraticFunction);

        assertEquals(3.0, derivative.getY(0), eRate);
        assertEquals(5.0, derivative.getY(1), eRate);
        assertEquals(7.0, derivative.getY(2), eRate);
        assertEquals(7.0, derivative.getY(3), eRate);
    }

    @Test
    void testDeriveWithUnevenSpacing() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {1.0, 1.5, 3.0, 5.0};
        double[] yValues = {1.0, 2.25, 9.0, 25.0};

        TabulatedFunction function = operator.getFactory().create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        double derivative0 = (2.25 - 1.0) / (1.5 - 1.0);
        double derivative1 = (9.0 - 2.25) / (3.0 - 1.5);
        double derivative2 = (25.0 - 9.0) / (5.0 - 3.0);
        double derivative3 = derivative2;

        assertEquals(derivative0, derivative.getY(0), eRate);
        assertEquals(derivative1, derivative.getY(1), eRate);
        assertEquals(derivative2, derivative.getY(2), eRate);
        assertEquals(derivative3, derivative.getY(3), eRate);
    }

    @Test
    void testDeriveWithAverageDifference() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};

        TabulatedFunction quadraticFunction = operator.getFactory().create(xValues, yValues);
        TabulatedFunction derivative = operator.deriveWithAverage(quadraticFunction);

        assertEquals(3.0, derivative.getY(0), eRate);
        assertEquals(4.0, derivative.getY(1), eRate);
        assertEquals(6.0, derivative.getY(2), eRate);
        assertEquals(7.0, derivative.getY(3), eRate);
    }

    @Test
    void testDeriveConstantFunction() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {5.0, 5.0, 5.0, 5.0};

        TabulatedFunction constantFunction = operator.getFactory().create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(constantFunction);

        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(0.0, derivative.getY(i), eRate);
        }
    }

    @Test
    void testDeriveLinearFunctionExact() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {2.0, 4.0, 6.0, 8.0};

        TabulatedFunction linearFunction = operator.getFactory().create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(linearFunction);

        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(2.0, derivative.getY(i), eRate);
        }
    }

    @Test
    void testDifferentFactoriesProduceSameResults() {
        TabulatedDifferentialOperator arrayOperator = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());
        TabulatedDifferentialOperator linkedOperator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 8.0, 27.0, 64.0};

        TabulatedFunction arrayFunction = arrayOperator.getFactory().create(xValues, yValues);
        TabulatedFunction linkedFunction = linkedOperator.getFactory().create(xValues, yValues);

        TabulatedFunction arrayDerivative = arrayOperator.derive(arrayFunction);
        TabulatedFunction linkedDerivative = linkedOperator.derive(linkedFunction);

        assertEquals(arrayDerivative.getCount(), linkedDerivative.getCount());
        for (int i = 0; i < arrayDerivative.getCount(); i++) {
            assertEquals(arrayDerivative.getX(i), linkedDerivative.getX(i), eRate);
            assertEquals(arrayDerivative.getY(i), linkedDerivative.getY(i), eRate);
        }
    }

    @Test
    void testDerivePreservesXValues() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] originalX = {0.1, 0.5, 1.0, 2.0, 5.0};
        double[] yValues = {0.01, 0.25, 1.0, 4.0, 25.0};

        TabulatedFunction function = operator.getFactory().create(originalX, yValues);
        TabulatedFunction derivative = operator.derive(function);

        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(originalX[i], derivative.getX(i), eRate);
        }
    }

    @Test
    void testConstructorWithFactory() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        assertSame(factory, operator.getFactory());
    }

    @Test
    void testDefaultConstructor() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        assertNotNull(operator.getFactory());
        assertTrue(operator.getFactory() instanceof ArrayTabulatedFunctionFactory);
    }

    @Test
    void testSetFactory() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        TabulatedFunctionFactory newFactory = new LinkedListTabulatedFunctionFactory();

        operator.setFactory(newFactory);

        assertSame(newFactory, operator.getFactory());
    }

    @Test
    void testDeriveSynchronouslyWithRegularFunction() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {0, 1, 2, 3, 4};
        double[] yValues = {0, 1, 4, 9, 16}; // y = x^2
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.deriveSynchronously(function);

        assertEquals(5, derivative.getCount());
        assertEquals(1.0, derivative.getY(0), 1e-9); // производная в x=0: (1-0)/1 = 1
        assertEquals(3.0, derivative.getY(1), 1e-9); // производная в x=1: (4-1)/1 = 3
        assertEquals(5.0, derivative.getY(2), 1e-9); // производная в x=2: (9-4)/1 = 5
    }

    @Test
    void testDeriveSynchronouslyWithSynchronizedFunction() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {0, 2, 4, 6};
        double[] yValues = {0, 4, 16, 36};
        TabulatedFunction baseFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        TabulatedFunction derivative = operator.deriveSynchronously(syncFunction);


        assertEquals(4, derivative.getCount());
        assertEquals(2.0, derivative.getY(0), 1e-9); // (4-0)/(2-0) = 2.0
        assertEquals(6.0, derivative.getY(1), 1e-9); // (16-4)/(4-2) = 6.0
        assertEquals(10.0, derivative.getY(2), 1e-9); // (36-16)/(6-4) = 10.0
        assertEquals(10.0, derivative.getY(3), 1e-9); // последняя точка: (36-16)/(6-4) = 10.0
    }

    @Test
    void testDeriveSynchronouslyWithLinearFunction() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        // Линейная функция y = 2x + 1
        double[] xValues = {0, 1, 2, 3, 4};
        double[] yValues = {1, 3, 5, 7, 9};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.deriveSynchronously(function);

        // Производная линейной функции должна быть постоянной
        assertEquals(5, derivative.getCount());
        assertEquals(2.0, derivative.getY(0), 1e-9); // (3-1)/1 = 2
        assertEquals(2.0, derivative.getY(1), 1e-9); // (5-3)/1 = 2
        assertEquals(2.0, derivative.getY(2), 1e-9); // (7-5)/1 = 2
        assertEquals(2.0, derivative.getY(3), 1e-9); // (9-7)/1 = 2
        assertEquals(2.0, derivative.getY(4), 1e-9); // (9-7)/1 = 2
    }
}