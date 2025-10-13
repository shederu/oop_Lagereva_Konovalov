package ru.ssau.tk._shederu_._lab1_.operations;

import ru.ssau.tk._shederu_._lab1_.functions.*;
import ru.ssau.tk._shederu_._lab1_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._shederu_._lab1_.functions.factory.ArrayTabulatedFunctionFactory;

import java.util.ArrayList;
import java.util.List;

public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction> {
    private TabulatedFunctionFactory factory;

    private final double eRate = 1e-9;

    public TabulatedDifferentialOperator() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    @Override
    public TabulatedFunction derive(TabulatedFunction function) {
        List<Point> points = new ArrayList<>();
        for (Point point : function) {
            points.add(point);
        }

        int pointCount = points.size();
        double[] xValues = new double[pointCount];
        double[] yValues = new double[pointCount];

        for (int i = 0; i < pointCount; i++) {
            xValues[i] = points.get(i).x;
        }

        for (int i = 0; i < pointCount - 1; i++) {
            double h = xValues[i + 1] - xValues[i];
            yValues[i] = (points.get(i + 1).y - points.get(i).y) / h;
        }

        double hLast = xValues[pointCount - 1] - xValues[pointCount - 2];
        yValues[pointCount - 1] = (points.get(pointCount - 1).y - points.get(pointCount - 2).y) / hLast;

        return factory.create(xValues, yValues);
    }

    public TabulatedFunction deriveWithAverage(TabulatedFunction function) {
        List<Point> points = new ArrayList<>();
        for (Point point : function) {
            points.add(point);
        }

        int pointCount = points.size();
        double[] xValues = new double[pointCount];
        double[] yValues = new double[pointCount];

        for (int i = 0; i < pointCount; i++) {
            xValues[i] = points.get(i).x;
        }

        for (int i = 1; i < pointCount - 1; i++) {
            double hLeft = xValues[i] - xValues[i - 1];
            double hRight = xValues[i + 1] - xValues[i];
            yValues[i] = ((points.get(i + 1).y - points.get(i).y) / hRight +
                    (points.get(i).y - points.get(i - 1).y) / hLeft) / 2;
        }

    yValues[0] = (points.get(1).y - points.get(0).y) / (xValues[1] - xValues[0]);

         yValues[pointCount - 1] = (points.get(pointCount - 1).y - points.get(pointCount - 2).y) / (xValues[pointCount - 1] - xValues[pointCount - 2]);

        return factory.create(xValues, yValues);
    }
}