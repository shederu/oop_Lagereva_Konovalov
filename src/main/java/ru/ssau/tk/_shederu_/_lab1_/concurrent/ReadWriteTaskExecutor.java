package ru.ssau.tk._shederu_._lab1_.concurrent;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.functions.ConstantFunction;
import ru.ssau.tk._shederu_._lab1_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

public class ReadWriteTaskExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ReadWriteTaskExecutor.class);

    public static void main(String[] args) {
        logger.info("Запуск ReadWriteTaskExecutor");

        ConstantFunction constFunc = new ConstantFunction(-1);
        logger.debug("Создана ConstantFunction: значение={}", -1);

        TabulatedFunction tabFunc = new LinkedListTabulatedFunction(constFunc, 1, 1000, 1000);
        logger.info("Создана TabulatedFunction: {} точек, диапазон=[{}, {}], тип={}", tabFunc.getCount(), tabFunc.leftBound(), tabFunc.rightBound(), tabFunc.getClass().getSimpleName());

        Object lock = new Object();
        logger.debug("Создан объект-блокировка");

        ReadTask readTask = new ReadTask(tabFunc, lock);
        WriteTask writeTask = new WriteTask(tabFunc, 0.5, lock);
        logger.debug("Созданы задачи: ReadTask и WriteTask");

        Thread readThread = new Thread(readTask);
        Thread writeThread = new Thread(writeTask);
        logger.info("Созданы потоки: {} и {}", readThread.getName(), writeThread.getName());

        logger.info("Запуск потоков");
        readThread.start();
        writeThread.start();

        try {
            logger.debug("Ожидание завершения потоков");
            readThread.join();
            logger.info("ReadThread завершился");

            writeThread.join();
            logger.info("WriteThread завершился");
        } catch (InterruptedException e) {
            logger.error("Главный поток был прерван", e);
            Thread.currentThread().interrupt();
        }
        logger.info("ReadWriteTaskExecutor завершил работу");
        logger.info("Финальное состояние функции: точек={}, первое значение y={}", tabFunc.getCount(), tabFunc.getY(0));
    }
}