package ru.ssau.tk._shederu_._lab1_.operations;

import ru.ssau.tk._shederu_._lab1_.functions.MathFunctions;

public interface DifferentialOperator<T extends MathFunctions> {
    T derive(T function);
}
