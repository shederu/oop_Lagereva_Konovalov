package ru.ssau.tk._shederu_._lab1_.functions;

public class RungeCute implements MathFunctions {

   private final MathFunctions f0;
   private final double x0;
   private final double y0;
   private final double h;

   public RungeCute(MathFunctions f, double x, double y, double h){
      this.f0 = f;
      this.x0 = x;
      this.y0 = y;
      this.h = h;
  }

    @Override
    public double apply(double x) {
       double k1, k2, k3, k4;
       double xi = x;
       double yi = y0;
       while (xi<x){
            k1 = h*f0.apply(xi);
            k2 = h*f0.apply(xi+h/2.0);
            k3 = h*f0.apply(xi+h/2.0);
            k4 = h*f0.apply(xi+h/2.0);
            xi += xi+h;
            yi += (k1 + 2*k2 + 2*k3 + k4) / 6;
       }
        return yi;
   }
}