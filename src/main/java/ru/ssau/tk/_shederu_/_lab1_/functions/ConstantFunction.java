package ru.ssau.tk._shederu_._lab1_.functions;

public class ConstantFunction implements MathFunctions{
    private final double constant;

    public ConstantFunction(double x){
        this.constant = x;
    }

    @Override
    public double apply(double x){
        return constant;
    }

    public double getconst(){
        return constant;
    }
}
