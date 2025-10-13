package ru.ssau.tk._shederu_._lab1_.operations;

import ru.ssau.tk._shederu_._lab1_.functions.MathFunctions;

public abstract class SteppingDifferentialOperator implements DifferentialOperator<MathFunctions> {

    protected double step;

    public SteppingDifferentialOperator(double step1) {
        if (Double.isNaN(step1) || Double.isInfinite(step1) || step1 <= 0) {
            throw new IllegalArgumentException();
        }
        this.step = step1;
    }

    public SteppingDifferentialOperator() {
        if (Double.isNaN(step) || Double.isInfinite(step) || step <= 0) {
            throw new IllegalArgumentException("Step must be positive finite number");
        }
        this.step = step;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step2) {
        if ((step2 <= 0.0) || Double.isInfinite(step2) || Double.isNaN(step2)) {
            throw new IllegalArgumentException("Invalid argument value");
        }
        this.step = step2;
    }
}