package ru.ssau.tk._shederu_._lab1_.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

public class WriteTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WriteTask.class);

    private TabulatedFunction func;
    private final double value;
    private Object lock;

    public WriteTask(TabulatedFunction func, double value, Object lock) {
        this.func = func;
        this.value = value;
        this.lock = lock;
        logger.debug("WriteTask создан: значение={}, функция={}", value, func.getClass().getSimpleName());
    }
    @Override
    public void run() {
        logger.info("WriteTask начал выполнение в потоке: {}", Thread.currentThread().getName());

        try {
            for (int i = 0; i < func.getCount(); i++) {
                synchronized (lock) {
                    double oldValue = func.getY(i);
                    func.setY(i, value);
                    logger.debug("Запись: i={}, {} -> {}", i, oldValue, value);
                }

                Thread.sleep(1);
            }
            logger.info("WriteTask завершил запись всех {} точек", func.getCount());
        } catch (InterruptedException e) {
            logger.warn("WriteTask прерван в потоке: {}", Thread.currentThread().getName(), e);
            Thread.currentThread().interrupt();

        } catch (Exception e) {
            logger.error("Ошибка в WriteTask", e);
        }
        logger.info("WriteTask завершил выполнение");
    }
}
