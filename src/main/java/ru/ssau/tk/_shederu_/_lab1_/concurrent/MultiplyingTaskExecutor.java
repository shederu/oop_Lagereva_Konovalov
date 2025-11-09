package ru.ssau.tk._shederu_._lab1_.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.functions.*;
import java.util.ArrayList;
import java.util.List;

public class MultiplyingTaskExecutor {
    private static final Logger logger = LoggerFactory.getLogger(MultiplyingTaskExecutor.class);

    public static void main(String[] args){
        logger.info("Запуск MultiplyingTaskExecutor");

        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(), 1, 1000, 1000);

        logger.info("Создана функция: {} точек, диапазон=[{}, {}]", function.getCount(), function.leftBound(), function.rightBound());

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10; i++){
            MultiplyingTask task = new MultiplyingTask(function);
            Thread thread = new Thread(task);
            threads.add(thread);
            logger.trace("Создан поток: {}", thread.getName());
        }

        logger.info("Запуск потоков");
        for(Thread thread: threads){
            thread.start();
            logger.debug("Запущен поток: {}", thread.getName());
        }

        logger.debug("Ожидание выполнения потоков (2 секунды)");
        try{
            Thread.sleep(2000);
        } catch (InterruptedException exc){
            logger.error("Прерывание ожидания потоков", exc);
            exc.printStackTrace();
        }

        logger.info("Проверка результатов");
        double expectedValue = Math.pow(2, 10);
        double actualValue = function.getY(0);
        logger.info("Ожидаемое значение y: {}", expectedValue);
        logger.info("Фактическое значение y в первой точке: {}", actualValue);

        int aliveThreads = 0;
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                aliveThreads++;
                logger.warn("Поток {} все еще выполняется", thread.getName());
            }
        }

        if (Math.abs(expectedValue - actualValue) < 1e-10) {
            logger.info("✓ Результат корректен");
        } else {
            logger.warn("✗ Несоответствие результатов: ожидалось {}, получено {}", expectedValue, actualValue);
        }


        if (aliveThreads == 0) {
            logger.info("Все потоки завершили выполнение");
        } else {
            logger.warn("{} потоков все еще выполняются", aliveThreads);
        }

        logger.info("Завершение MultiplyingTaskExecutor");
    }
}
