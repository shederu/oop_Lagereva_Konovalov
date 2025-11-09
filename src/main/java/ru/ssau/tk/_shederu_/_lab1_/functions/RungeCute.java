package ru.ssau.tk._shederu_._lab1_.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RungeCute implements MathFunctions {
    private static final Logger logger = LoggerFactory.getLogger(RungeCute.class);

    private final MathFunctions f0;
    private final double x0;
    private final double y0;
    private final double step;

    public RungeCute(MathFunctions f, double x, double y, double step){
        logger.debug("Создание RungeCute: x0={}, y0={}, step={}", x, y, step);

        if (f == null) {
            logger.error("Передана null функция");
            throw new IllegalArgumentException("Функция должна передаваться");
        }

        if (step == 0) {
            logger.error("Нулевой шаг: step={}", step);
            throw new IllegalArgumentException("Шаг не может быть нулевым");
        }

        this.f0 = f;
        this.x0 = x;
        this.y0 = y;
        this.step = step;

        logger.info("RungeCute инициализирован: функция={}, начальные условия=[{}, {}], шаг={}", f.getClass().getSimpleName(), x, y, step);
    }

    @Override
    public double apply(double x) {
        logger.debug("Вычисление методом Рунге-Кутты для x={}", x);

        double k1, k2, k3, k4;
        double xi = x0;
        double yi = y0;

        double h = Math.abs(step) * ((x > x0)? 1.0: 0.0);
        int n = (int) Math.ceil(Math.abs(x-x0)/Math.abs(step));

        logger.trace("Параметры вычисления: h={}, n={} итераций", h, n);

        if (n > 10000) {
            logger.warn("Большое количество итераций: n={}. Возможно, слишком маленький шаг.", n);
        }

        for(int i = 0; i < n; i++){
            k1 = h * f0.apply(xi);
            k2 = h * f0.apply(xi+h/2.0);
            k3 = h * f0.apply(xi+h/2.0);
            k4 = h * f0.apply(xi+h);

            if (Double.isNaN(k1) || Double.isNaN(k2) || Double.isNaN(k3) || Double.isNaN(k4)) {
                logger.error("Обнаружены NaN значения на итерации {}: k1={}, k2={}, k3={}, k4={}", i, k1, k2, k3, k4);
                return Double.NaN;
            }

            xi += h;
            yi += (k1 + 2*k2 + 2*k3 + k4) / 6;

            logger.trace("Итерация {}: xi={}, yi={}, k1={}, k2={}, k3={}, k4={}", i, xi, yi, k1, k2, k3, k4);

            if (Double.isInfinite(yi)) {
                logger.error("Расходимость алгоритма на итерации {}", i);
                return Double.NaN;
            }
        }

        logger.debug("Вычисление завершено: x={}, результат={}", x, yi);
        return yi;
    }
}