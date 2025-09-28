package ru.ssau.tk._shederu_._lab1_.functions;

public abstract class AbstractTabulatedFunction implements TabulatedFunction, MathFunctions{
    protected abstract int floorIndexOfX(double x);
    protected abstract double extrapolateLeft(double x);
    protected abstract double extrapolateRight(double x);
    protected abstract double interpolate(double x, int floorIndex);

    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        if (Math.abs(rightX - leftX) < 1e-10) {
            throw new IllegalArgumentException("Интервал интерполяции должен быть больше нуля: leftX = " + leftX + ", rightX = " + rightX);
        }
        return leftY + (rightY - leftY) * (x - leftX) / (rightX - leftX);
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
}
