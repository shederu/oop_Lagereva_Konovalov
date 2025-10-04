package ru.ssau.tk._shederu_._lab1_.functions;

import ru.ssau.tk._shederu_._lab1_.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk._shederu_._lab1_.exceptions.DifferentLengthOfArraysException;
import ru.ssau.tk._shederu_._lab1_.exceptions.InterpolationException;

public abstract class AbstractTabulatedFunction implements TabulatedFunction, MathFunctions{
    protected abstract int floorIndexOfX(double x);
    protected abstract double extrapolateLeft(double x);
    protected abstract double extrapolateRight(double x);
    protected abstract double interpolate(double x, int floorIndex);

    protected double interpolate(double x, double x0, double x1, double y0, double y1) {
        if (Math.abs(x1 - x0) < 1e-10) {
            throw new InterpolationException("Интервал интерполяции должен быть больше нуля");
        }
        if (x < x0 || x > x1){
            throw new InterpolationException("Х должен находиться в пределах интервала интерполирования.");
        }
        return y0 + (y1 - y0) * (x - x0) / (x1 - x0);
    }

    @Override
    public double apply(double x) {
        if (x < leftBound()) {
            return extrapolateLeft(x);
        }
        else if (x > rightBound()) {
            return extrapolateRight(x);
        }
        else {
            int index = indexOfX(x);
            if (index != -1) {
                return getY(index);
            }
            else {
                return interpolate(x, floorIndexOfX(x));
            }
        }
    }

    public static void checkLengthIsTheSame(double[] xValues, double[] yValues){
        if(xValues.length != yValues.length){
            throw new DifferentLengthOfArraysException("Разная длина массивавю");
        }
    }

    public static void checkSorted(double[] xValues){
        for(int i = 0; i < xValues.length-1; i++){
            if(xValues[i] >= xValues[i+1]){
                throw new ArrayIsNotSortedException("Массив не отсортирован.");
            }
        }
    }
}
