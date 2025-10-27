package ru.ssau.tk._shederu_._lab1_.concurrent;


import ru.ssau.tk._shederu_._lab1_.functions.ConstantFunction;
import ru.ssau.tk._shederu_._lab1_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

public class ReadWriteTaskExecutor {
    public static void main(String[] args) {

        ConstantFunction constFunc = new ConstantFunction(-1);

        TabulatedFunction tabFunc = new LinkedListTabulatedFunction(constFunc, 1, 1000, 1000);

        Object lock = new Object();

        ReadTask readTask = new ReadTask(tabFunc, lock);
        WriteTask writeTask = new WriteTask(tabFunc, 0.5, lock);

        Thread readThread = new Thread(readTask);
        Thread writeThread = new Thread(writeTask);
        readThread.start();
        writeThread.start();

        try {
            // Ждем завершения потоков
            readThread.join();
            writeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}