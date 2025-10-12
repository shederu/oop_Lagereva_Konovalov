package ru.ssau.tk._shederu_._lab1_.functions.factory;

import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

public interface TabulatedFunctionFactory {
    TabulatedFunction create(double[] xValues, double[] yValues);
}
