package ru.ssau.tk._shederu_._lab1_.functions;

public interface MathFunctions{
    double apply(double x);

    default MathFunctions andThen(MathFunctions afterFunction) {
        return new CompositeFunction(this, afterFunction);
    }
}
