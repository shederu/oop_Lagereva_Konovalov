package ru.ssau.tk._shederu_._lab1_.functions.factory;

import ru.ssau.tk._shederu_._lab1_.functions.MathFunctions;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

public interface TabulatedFunctionFactory {
    TabulatedFunction create(double[] x, double[] y);

    TabulatedFunction create(MathFunctions function, double min, double max, int pointCount);
}
