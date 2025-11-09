package ru.ssau.tk._shederu_._lab1_.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

public class MultiplyingTask implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(MultiplyingTask.class);

    private final TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function){
        this.function = function;
        logger.info("Создана задача MultiplyingTask для функции с {} точками", function.getCount());
    }

    @Override
    public void run(){
        logger.info("Запуск задачи умножения в потоке: {}", Thread.currentThread().getName());

        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                double currentY = function.getY(i);
                function.setY(i, currentY * 2);
                logger.trace("Умножено значение Y[{}]: {} -> {}", i, currentY, function.getY(i));
            }
        }

        logger.info("Задача умножения завершена в потоке: {}", Thread.currentThread().getName());
        System.out.println("Поток " + Thread.currentThread().getName() + " закончил выполнение задачи.");
    }
}
