package ru.ssau.tk._shederu_._lab1_.functions.factory;

import ru.ssau.tk._shederu_._lab1_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.MathFunctions;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

public class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {

    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        return new LinkedListTabulatedFunction(xValues, yValues);
    }

    @Override
    public TabulatedFunction create(MathFunctions function, double min, double max, int pointCount) {
        return new LinkedListTabulatedFunction(function, min, max, pointCount);
    }
}
