package ru.ssau.tk._shederu_._lab1_.concurrent;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

public class ReadTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ReadTask.class);

    private TabulatedFunction func;
    private Object lock;

    public ReadTask(TabulatedFunction func, Object lock) {
        this.func = func;
        this.lock = lock;
        logger.debug("ReadTask создан: функция={}, точек={}", func.getClass().getSimpleName(), func.getCount());
    }

    @Override
    public void run() {
        logger.info("ReadTask начал выполнение в потоке: {}", Thread.currentThread().getName());

        try {
            // Читаем значения для всех индексов
            for (int i = 0; i < func.getCount(); i++) {
                synchronized (lock) {
                    double x = func.getX(i);
                    double y = func.getY(i);
                    logger.debug("Прочитано: i={}, x={}, y={}", i, x, y);
                }
                
                Thread.sleep(1);
            }
            logger.info("ReadTask завершил чтение всех точек");
        } catch (InterruptedException e) {
            logger.warn("ReadTask прерван в потоке: {}", Thread.currentThread().getName(), e);
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Ошибка в ReadTask", e);
        }

        logger.info("ReadTask завершил выполнение");
    }
}
