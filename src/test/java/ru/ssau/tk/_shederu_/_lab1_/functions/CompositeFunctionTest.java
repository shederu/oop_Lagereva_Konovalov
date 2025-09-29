package ru.ssau.tk._shederu_._lab1_.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionTest {
    private static final double eRate = 1e-9;

    @Test
    void testLinearComposition() {
        MathFunctions multiplyByTwo = x -> 2 * x;
        MathFunctions addThree = x -> x + 3;
        CompositeFunction composite = new CompositeFunction(addThree, multiplyByTwo);

        assertEquals(10.0, composite.apply(2.0), eRate);
        assertEquals(6.0, composite.apply(0.0), eRate);
        assertEquals(2.0, composite.apply(-2.0), eRate);
    }

    @Test
    void testCubicAndSquareComposition() {
        MathFunctions square = new SqrFunction();
        MathFunctions cubic = x -> x * x * x;

        CompositeFunction cubicThenSquare = new CompositeFunction(square, cubic);
        CompositeFunction squareThenCubic = new CompositeFunction(cubic, square);

        assertEquals(64.0, cubicThenSquare.apply(2.0), eRate);
        assertEquals(64.0, squareThenCubic.apply(2.0), eRate);
        assertEquals(1.0, cubicThenSquare.apply(1.0), eRate);
        assertEquals(1.0, squareThenCubic.apply(1.0), eRate);
    }

    @Test
    void testMultipleComposition() {
        MathFunctions square = new SqrFunction();
        MathFunctions identity = new IdentityFunction();

        CompositeFunction firstLevel = new CompositeFunction(square, identity);
        CompositeFunction secondLevel = new CompositeFunction(firstLevel, square);

        assertEquals(16.0, secondLevel.apply(2.0), eRate);
        assertEquals(81.0, secondLevel.apply(3.0), eRate);
        assertEquals(0.0, secondLevel.apply(0.0), eRate);
    }

    @Test
    void testCompositeOfCompositeFunctions() {
        MathFunctions addTwo = x -> x + 2;
        MathFunctions square = new SqrFunction();
        CompositeFunction firstComposite = new CompositeFunction(square, addTwo);

        MathFunctions multiplyByThree = x -> 3 * x;
        MathFunctions subtractOne = x -> x - 1;
        CompositeFunction secondComposite = new CompositeFunction(subtractOne, multiplyByThree);

        CompositeFunction compositeOfComposites = new CompositeFunction(secondComposite, firstComposite);

        assertEquals(2.0, compositeOfComposites.apply(1.0), eRate);
        assertEquals(11.0, compositeOfComposites.apply(2.0), eRate);
        assertEquals(38.0, compositeOfComposites.apply(3.0), eRate);
    }

    @Test
    void testExponentialComposition() {
        MathFunctions doubleFunc = x -> 2 * x;
        MathFunctions exponential = x -> Math.exp(x);

        CompositeFunction expThenDouble = new CompositeFunction(doubleFunc, exponential);
        CompositeFunction doubleThenExp = new CompositeFunction(exponential, doubleFunc);

        assertEquals(1.0, expThenDouble.apply(0.0), eRate);
        assertEquals(5.436, doubleThenExp.apply(1.0), 0.01);
        assertEquals(0.0, expThenDouble.apply(-100.0), eRate);
    }

    @Test
    void testInverseComposition() {
        MathFunctions addFive = x -> x + 5;
        MathFunctions inverse = x -> x != 0 ? 1 / x : 0;

        CompositeFunction inverseThenAdd = new CompositeFunction(addFive, inverse);
        CompositeFunction addThenInverse = new CompositeFunction(inverse, addFive);

        assertEquals(0.1, inverseThenAdd.apply(5.0), eRate);
        assertEquals(5.2, addThenInverse.apply(5.0), eRate);
        assertEquals(0.2, inverseThenAdd.apply(0.0), eRate);
    }

    @Test
    void testTabulatedFunctionsComposition() {
        // f(x) = x + 1
        double[] x1 = {0.0, 1.0, 2.0, 3.0};
        double[] y1 = {1.0, 2.0, 3.0, 4.0};
        ArrayTabulatedFunction f = new ArrayTabulatedFunction(x1, y1);

        // g(x) = 2x
        double[] x2 = {0.0, 1.0, 2.0, 3.0};
        double[] y2 = {0.0, 2.0, 4.0, 6.0};
        LinkedListTabulatedFunction g = new LinkedListTabulatedFunction(x2, y2);

        // h(x) = f(g(x)) = (2x) + 1
        CompositeFunction composition = new CompositeFunction(f, g);


        assertEquals(2.0, composition.apply(0.0), eRate);
        assertEquals(4.0, composition.apply(1.0), eRate);
        assertEquals(6.0, composition.apply(2.0), eRate);
    }


    @Test
    void testIdentityComposition() {
        MathFunctions identity = new IdentityFunction();
        CompositeFunction doubleIdentity = new CompositeFunction(identity, identity);

        assertEquals(5.0, doubleIdentity.apply(5.0), eRate);
        assertEquals(-3.0, doubleIdentity.apply(-3.0), eRate);
        assertEquals(0.0, doubleIdentity.apply(0.0), eRate);
    }

    @Test
    void testConstantComposition() {
        MathFunctions constant = new ConstantFunction(5.0);
        MathFunctions square = new SqrFunction();

        CompositeFunction constThenSquare = new CompositeFunction(square, constant);
        CompositeFunction squareThenConst = new CompositeFunction(constant, square);

        assertEquals(5.0, constThenSquare.apply(10.0), eRate);
        assertEquals(25.0, squareThenConst.apply(10.0), eRate);
        assertEquals(5.0, constThenSquare.apply(-10.0), eRate);
        assertEquals(25.0, squareThenConst.apply(-10.0), eRate);
    }

    @Test
    void testEmptyFunctionComposition() {
        MathFunctions zero = new ZeroFunction();
        MathFunctions identity = new IdentityFunction();

        CompositeFunction zeroThenIdentity = new CompositeFunction(identity, zero);
        CompositeFunction identityThenZero = new CompositeFunction(zero, identity);

        assertEquals(0.0, zeroThenIdentity.apply(5.0), eRate);
        assertEquals(0.0, identityThenZero.apply(5.0), eRate);
        assertEquals(0.0, zeroThenIdentity.apply(-5.0), eRate);
        assertEquals(0.0, identityThenZero.apply(-5.0), eRate);
    }

    @Test
    void testComplexNestedComposition() {
        MathFunctions square = new SqrFunction();
        MathFunctions increment = x -> x + 1;
        MathFunctions doubleFunc = x -> 2 * x;

        CompositeFunction first = new CompositeFunction(square, increment);
        CompositeFunction second = new CompositeFunction(doubleFunc, first);
        CompositeFunction third = new CompositeFunction(second, square);

        assertEquals(289.0, third.apply(2.0), eRate);
        assertEquals(1369.0, third.apply(3.0), eRate);
        assertEquals(25.0, third.apply(-1.0), eRate);
    }

    @Test
    void testEdgeCases() {
        MathFunctions identity = new IdentityFunction();
        MathFunctions constant = new ConstantFunction(0.0);

        CompositeFunction composite = new CompositeFunction(constant, identity);

        assertEquals(0.0, composite.apply(Double.MAX_VALUE), eRate);
        assertEquals(0.0, composite.apply(Double.MIN_VALUE), eRate);
        assertEquals(0.0, composite.apply(0.0), eRate);
    }

    @Test
    void testSameFunctionRepeated() {
        MathFunctions square = new SqrFunction();

        CompositeFunction squareSquare = new CompositeFunction(square, square);
        CompositeFunction squareSquareSquare = new CompositeFunction(squareSquare, square);

        assertEquals(16.0, squareSquare.apply(2.0), eRate);
        assertEquals(256.0, squareSquareSquare.apply(2.0), eRate);
        assertEquals(1.0, squareSquare.apply(1.0), eRate);
        assertEquals(1.0, squareSquareSquare.apply(1.0), eRate);
    }
}
