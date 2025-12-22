package ru.ssau.tk._shederu_._lab1_.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилита для вычисления производной табулированной функции
 * по формуле: ỹ_i = (y_{i+1} - y_i) / (x_{i+1} - x_i)
 */
public class DerivativeCalculator {

    /**
     * Вычисляет производную функции
     * @param xValues значения X (уже отсортированные)
     * @param yValues значения Y
     * @return список значений производной
     */
    public static List<Double> calculateDerivative(List<Double> xValues, List<Double> yValues) {
        if (xValues == null || yValues == null) {
            throw new IllegalArgumentException("X и Y не должны быть null");
        }

        if (xValues.size() < 2 || yValues.size() < 2) {
            throw new IllegalArgumentException("Для вычисления производной нужно минимум 2 точки");
        }

        if (xValues.size() != yValues.size()) {
            throw new IllegalArgumentException("Размеры X и Y должны совпадать");
        }

        List<Double> derivative = new ArrayList<>();

        // Вычисляем производную по формуле ỹ_i = (y_{i+1} - y_i) / (x_{i+1} - x_i)
        for (int i = 0; i < xValues.size() - 1; i++) {
            double dx = xValues.get(i + 1) - xValues.get(i);
            double dy = yValues.get(i + 1) - yValues.get(i);

            if (dx == 0) {
                throw new IllegalArgumentException("Деление на ноль: x[" + i + "] == x[" + (i + 1) + "]");
            }

            double derivativeValue = dy / dx;
            derivative.add(derivativeValue);
        }

        // Дублируем последнее значение для совпадения размера
        if (!derivative.isEmpty()) {
            derivative.add(derivative.get(derivative.size() - 1));
        }

        return derivative;
    }
}
