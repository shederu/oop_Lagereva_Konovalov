package ru.ssau.tk._shederu_._lab1_.operations;

import ru.ssau.tk._shederu_._lab1_.functions.MathFunctions;

public class LeftSteppingDifferentialOperator extends SteppingDifferentialOperator {
    public LeftSteppingDifferentialOperator(double step1) {
        super(step1);
    }

    public LeftSteppingDifferentialOperator() {
        super();
    }

    @Override
    public MathFunctions derive(MathFunctions function) {
        return new MathFunctions() {
            @Override
            public double apply(double x) {
                // Левая разностная производная:
                // f'(x) ≈ (f(x) - f(x - h)) / h
                double h = step;
                return (function.apply(x) - function.apply(x - h)) / h;
            }
        };
    }
}
