package ru.ssau.tk._shederu_._lab1_.functions;

public class RungeCute implements MathFunctions {

    private final MathFunctions f0;
    private final double x0;
    private final double y0;
    private final double step;

    public RungeCute(MathFunctions f, double x, double y, double step){
        this.f0 = f;
        this.x0 = x;
        this.y0 = y;
        this.step = step;
    }

    @Override
    public double apply(double x) {
        double k1, k2, k3, k4;
        double xi = x0;
        double yi = y0;

        double h = Math.abs(step) * ((x > x0)? 1.0: 0.0);
        int n = (int) Math.ceil(Math.abs(x-x0)/Math.abs(step));

        for(int i = 0; i < n; i++){
            k1 = h * f0.apply(xi);
            k2 = h * f0.apply(xi+h/2.0);
            k3 = h * f0.apply(xi+h/2.0);
            k4 = h * f0.apply(xi+h);

            xi += h;
            yi += (k1 + 2*k2 + 2*k3 + k4) / 6;
        }
        return yi;
    }
}