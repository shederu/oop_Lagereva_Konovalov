package ru.ssau.tk._shederu_._lab1_.functions;

public class RungeCute implements MathFunctions {

    private double x;
    private double y;
    private double h;
    private MathFunctions  f;

    public RungeCute{

    }

    public double calculate(MathFunctions  f,double h,double y, double x){

        double k1 = h*f.apply(x);
    }



    @Override
    public double apply(double  x){
        return x;
    }

}
