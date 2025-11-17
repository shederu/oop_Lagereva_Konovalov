package ru.ssau.tk._shederu_._lab1_.functions;
//работает??
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk._shederu_._lab1_.exceptions.DifferentLengthOfArraysException;
import ru.ssau.tk._shederu_._lab1_.exceptions.InterpolationException;

public abstract class AbstractTabulatedFunction implements TabulatedFunction, MathFunctions{
    private static final Logger logger = LoggerFactory.getLogger(AbstractTabulatedFunction.class);

    protected abstract int floorIndexOfX(double x);
    protected abstract double extrapolateLeft(double x);
    protected abstract double extrapolateRight(double x);
    protected abstract double interpolate(double x, int floorIndex);

    protected double interpolate(double x, double x0, double x1, double y0, double y1) {
        if (Math.abs(x1 - x0) < 1e-10) {
            logger.error("Нулевой интервал интерполяции: x0={}, x1={}", x0, x1);
            throw new InterpolationException("Интервал интерполяции должен быть больше нуля");
        }
        if (x < x0 || x > x1){
            logger.error("X вне интервала интерполяции: x={}, интервал=[{}, {}]", x, x0, x1);
            throw new InterpolationException("Х должен находиться в пределах интервала интерполирования.");
        }
        return y0 + (y1 - y0) * (x - x0) / (x1 - x0);
    }

    @Override
    public double apply(double x) {
        logger.trace("apply(x={})", x);

        if (x < leftBound()) {
            logger.debug("Экстраполяция слева: x={} < leftBound={}", x, leftBound());
            return extrapolateLeft(x);
        }
        else if (x > rightBound()) {
            logger.debug("Экстраполяция справа: x={} > rightBound={}", x, rightBound());
            return extrapolateRight(x);
        }
        else {
            int index = indexOfX(x);
            if (index != -1) {
                logger.trace("Точное значение: x={}, y={}", x, getY(index));
                return getY(index);
            }
            else {
                logger.debug("Интерполяция: x={}, floorIndex={}", x, floorIndexOfX(x));
                return interpolate(x, floorIndexOfX(x));
            }
        }
    }

    public static void checkLengthIsTheSame(double[] xValues, double[] yValues){
        if(xValues.length != yValues.length){
            logger.error("Разная длина массивов: xValues.length={}, yValues.length={}", xValues.length, yValues.length);
            throw new DifferentLengthOfArraysException("Разная длина массивавю");
        }
    }

    public static void checkSorted(double[] xValues){
        for(int i = 0; i < xValues.length-1; i++){
            if(xValues[i] >= xValues[i+1]){
                logger.error("Массив не отсортирован: xValues[{}]={} >= xValues[{}]={}", i, xValues[i], i+1, xValues[i+1]);
                throw new ArrayIsNotSortedException("Массив не отсортирован.");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" size = ").append(getCount()).append("\n");

        for (Point point : this) {
            sb.append("[").append(point.x).append("; ").append(point.y).append("]\n");
        }

        return sb.toString();
    }
}
