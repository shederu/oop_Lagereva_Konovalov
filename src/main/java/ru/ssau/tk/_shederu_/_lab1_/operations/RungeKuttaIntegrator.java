package ru.ssau.tk._shederu_._lab1_.operations;

import ru.ssau.tk._shederu_._lab1_.functions.*;
import ru.ssau.tk._shederu_._lab1_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._shederu_._lab1_.functions.factory.ArrayTabulatedFunctionFactory;
import java.util.ArrayList;
import java.util.List;

public class RungeKuttaIntegrator {

    private TabulatedFunctionFactory factory;
    private static final double DEFAULT_STEP = 0.1;

    public RungeKuttaIntegrator() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public RungeKuttaIntegrator(TabulatedFunctionFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Фабрика не может быть null");
        }
        this.factory = factory;
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Фабрика не может быть null");
        }
        this.factory = factory;
    }

    public TabulatedFunction integrate(MathFunctions f, double x0, double y0, double xEnd, double step) {
        if (f == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        if (step == 0) {
            throw new IllegalArgumentException("Шаг не может быть нулевым");
        }
        List<Double> xList = new ArrayList<>();
        List<Double> yList = new ArrayList<>();

        double x = x0;
        double y = y0;
        boolean forward = xEnd > x0;

        xList.add(x);
        yList.add(y);

        while ((forward && x < xEnd) || (!forward && x > xEnd)) {
            double h = step;

            // Подгоняем шаг, чтобы не перешагнуть границу
            if ((forward && x + h > xEnd) || (!forward && x + h < xEnd)) {
                h = xEnd - x;
            }

            // Метод Рунге-Кутта 4-го порядка
            double k1 = f.apply(x);
            double k2 = f.apply(x + h / 2.0);
            double k3 = f.apply(x + h / 2.0);
            double k4 = f.apply(x + h);

            y = y + (h / 6.0) * (k1 + 2.0 * k2 + 2.0 * k3 + k4);
            x = x + h;

            xList.add(x);
            yList.add(y);

            if ((forward && x >= xEnd) || (!forward && x <= xEnd)) {
                break;
            }
        }

        // Преобразуем в массивы
        double[] xArray = new double[xList.size()];
        double[] yArray = new double[yList.size()];

        for (int i = 0; i < xList.size(); i++) {
            xArray[i] = xList.get(i);
            yArray[i] = yList.get(i);
        }

        return factory.create(xArray, yArray);
    }

    public TabulatedFunction integrate(MathFunctions f, double x0, double y0, double xEnd) {
        double step = (xEnd > x0) ? Math.abs(DEFAULT_STEP) : -Math.abs(DEFAULT_STEP);
        return integrate(f, x0, y0, xEnd, step);
    }

    public TabulatedFunction integrateWithPoints(MathFunctions f, double x0, double y0, double xEnd, int pointCount) {
        if (pointCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть >= 2");
        }
        double step = (xEnd - x0) / (pointCount - 1.0);
        return integrate(f, x0, y0, xEnd, step);
    }

    public TabulatedFunction integrateTabulatedDerivative(TabulatedFunction derivativeFunction, double y0) {
        if (derivativeFunction == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }

        List<Point> points = new ArrayList<>();
        for (Point p : derivativeFunction) {
            points.add(p);
        }

        if (points.isEmpty()) {
            throw new IllegalArgumentException("Функция должна содержать хотя бы одну точку");
        }

        int n = points.size();
        double[] xArray = new double[n];
        double[] yArray = new double[n];

        xArray[0] = points.get(0).x;
        yArray[0] = y0;

        for (int i = 1; i < n; i++) {
            double x0 = xArray[i - 1];
            double x1 = points.get(i).x;
            double h = x1 - x0;
            double dy = (points.get(i - 1).y + points.get(i).y) / 2.0 * h;
            yArray[i] = yArray[i - 1] + dy;
            xArray[i] = x1;
        }

        return factory.create(xArray, yArray);
    }
}
