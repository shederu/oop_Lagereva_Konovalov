package ru.ssau.tk._shederu_._lab1_.functions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class RungeCuteTest{
    public double delta;

    @BeforeEach
    void setUp() {
        delta = 0.001;
    }

    @Test
    public void testConstantFunctionSolution() {
        //dy/dx = 0, y(0) = 7 -> y(x) = 7
        MathFunctions zeroFunction = x -> 0;

        RungeCute function = new RungeCute(zeroFunction, 0, 7, 0.1);
        assertEquals(7.0, function.apply(1.0), delta);
        assertEquals(7.0, function.apply(2.0), delta);
        assertEquals(7.0, function.apply(-1.0), delta);
        assertEquals(7.0, function.apply(10.0), delta);
        assertEquals(7.0, function.apply(0.5), delta);
        assertEquals(7.0, function.apply(100.0), delta);
    }

    @Test
    public void testQuadraticFunctionSolution() {
        //dy/dx = 2*x/5, y(0) = 0 -> y(x) = x²/5
        MathFunctions identity = x -> x*2/5;

        RungeCute function = new RungeCute(identity, 0, 0, 0.001);
        assertEquals(0.0, function.apply(0.0), delta);
        assertEquals(0.2, function.apply(1.0), delta);
        assertEquals(0.799, function.apply(2.0), delta);
        assertEquals(1.799, function.apply(3.0), delta);
        assertEquals(0.050, function.apply(0.5), delta);
        assertEquals(3.199, function.apply(4.0), delta);
    }

    @Test
    public void testComplexTrigonometricFunction() {
        //dy/dx = 2cos(x)+sin(x), y(0) = 0 -> y(x) = 2sin(x)-cos(x)-6
        MathFunctions derivative = x -> 2*java.lang.Math.cos(x) + java.lang.Math.sin(x);

        RungeCute solver = new RungeCute(derivative, 0, 0, 0.001);

        //Точное решение: y(x) = 2sin(x)-cos(x)-6
        assertEquals(0.0, solver.apply(0.0), delta);
        assertEquals(1.134, solver.apply(java.lang.Math.PI/6), delta);
        assertEquals(3.0, solver.apply(java.lang.Math.PI/2), delta);
        assertEquals(1.999, solver.apply(java.lang.Math.PI), delta);
        assertEquals(-1.0, solver.apply(3*java.lang.Math.PI/2), delta);
    }
}