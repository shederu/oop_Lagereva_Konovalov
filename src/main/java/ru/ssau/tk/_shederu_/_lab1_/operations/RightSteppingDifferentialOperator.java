package ru.ssau.tk._shederu_._lab1_.operations;

import ru.ssau.tk._shederu_._lab1_.functions.MathFunctions;

public class RightSteppingDifferentialOperator extends SteppingDifferentialOperator {
    public RightSteppingDifferentialOperator(double step1) {
        super(step1);
    }

    @Override
    public MathFunctions derive(MathFunctions function) {
        return new MathFunctions() {
            @Override
            public double apply(double x) {
                // Правая разностная производная:
                // f'(x) ≈ (f(x) - f(x - h)) / h
                double h = step;
                return (function.apply(x) - function.apply(x + h)) / h;
            }
        };
    }
}
