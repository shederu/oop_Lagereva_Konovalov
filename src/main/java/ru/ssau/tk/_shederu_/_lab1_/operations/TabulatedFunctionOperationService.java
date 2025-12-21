package ru.ssau.tk._shederu_._lab1_.operations;

import ru.ssau.tk._shederu_._lab1_.functions.*;
import ru.ssau.tk._shederu_._lab1_.functions.factory.*;
import ru.ssau.tk._shederu_._lab1_.exceptions.*;

public class TabulatedFunctionOperationService {

    public static Point[] asPoints(TabulatedFunction tabulatedFunction) {
        if (tabulatedFunction == null) {
            throw new NullPointerException("Табулированная функция не может быть null");
        }
        Point[] points = new Point[tabulatedFunction.getCount()];
        int i = 0;
        for (Point point : tabulatedFunction) {
            points[i] = point;
            i++;
        }
        return points;
    }

    TabulatedFunctionFactory factory;
    private RungeKuttaIntegrator rungeKuttaIntegrator;

    public TabulatedFunctionOperationService() {
        this.factory = new ArrayTabulatedFunctionFactory();
        this.rungeKuttaIntegrator = new RungeKuttaIntegrator(this.factory);
    }

    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Factory cannot be null");
        }
        this.factory = factory;
        this.rungeKuttaIntegrator = new RungeKuttaIntegrator(factory);
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Фабрика не может быть null");
        }
        this.factory = factory;
        this.rungeKuttaIntegrator.setFactory(factory);
    }

    public RungeKuttaIntegrator getRungeKuttaIntegrator() {
        return rungeKuttaIntegrator;
    }

    @FunctionalInterface
    private interface BiOperation {
        double apply(double u, double v);
    }

    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation operation) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Табулированная функция не может быть null");
        }
        int countA = a.getCount();
        int countB = b.getCount();
        if (countA != countB) {
            throw new InconsistentFunctionsException("Размеры не совпадают: " + countA + " и " + countB);
        }
        Point[] pointsA = asPoints(a);
        Point[] pointsB = asPoints(b);
        double[] xValues = new double[countA];
        double[] yValues = new double[countA];
        for (int i = 0; i < countA; i++) {
            double xA = pointsA[i].x;
            double xB = pointsB[i].x;
            if (xA != xB) {
                throw new InconsistentFunctionsException("X не совпадают!");
            }
            xValues[i] = xA;
            yValues[i] = operation.apply(pointsA[i].y, pointsB[i].y);
        }
        return factory.create(xValues, yValues);
    }

    public TabulatedFunction add(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (u, v) -> u + v);
    }

    public TabulatedFunction subtract(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (u, v) -> u - v);
    }

    public TabulatedFunction multiply(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (u, v) -> u * v);
    }

    public TabulatedFunction divide(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (u, v) -> {
            if (v == 0.0) {
                throw new ArithmeticException("Деление на ноль");
            }
            return u / v;
        });
    }

    public TabulatedFunction solveODE(MathFunctions f, double x0, double y0, double xEnd, double step) {
        if (f == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        return rungeKuttaIntegrator.integrate(f, x0, y0, xEnd, step);
    }

    public TabulatedFunction solveODE(MathFunctions f, double x0, double y0, double xEnd) {
        if (f == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        return rungeKuttaIntegrator.integrate(f, x0, y0, xEnd);
    }

    public TabulatedFunction solveODEWithPoints(MathFunctions f, double x0, double y0, double xEnd, int pointCount) {
        if (f == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        if (pointCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть >= 2");
        }
        return rungeKuttaIntegrator.integrateWithPoints(f, x0, y0, xEnd, pointCount);
    }

    public TabulatedFunction integrateFunction(TabulatedFunction derivativeFunction, double y0) {
        if (derivativeFunction == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        return rungeKuttaIntegrator.integrateTabulatedDerivative(derivativeFunction, y0);
    }
}