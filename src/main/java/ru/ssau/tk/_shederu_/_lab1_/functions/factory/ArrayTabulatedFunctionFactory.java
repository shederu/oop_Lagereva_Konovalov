package ru.ssau.tk._shederu_._lab1_.functions.factory;

import ru.ssau.tk._shederu_._lab1_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

public class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {

    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        return new ArrayTabulatedFunction(xValues, yValues);
    }
}
