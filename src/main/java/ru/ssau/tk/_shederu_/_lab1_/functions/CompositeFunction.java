package ru.ssau.tk._shederu_._lab1_.functions;

public class CompositeFunction implements MathFunctions{

    private MathFunctions firstFunction;
    private MathFunctions secondFunction;

    public CompositeFunction(MathFunctions g, MathFunctions f ){
        this.firstFunction = f;
        this.secondFunction = g;
    }

    @Override
    public double apply(double x){
        return secondFunction.apply(firstFunction.apply(x));
    }
}
