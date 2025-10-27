package ru.ssau.tk._shederu_._lab1_.concurrent;


import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

public class ReadTask implements Runnable {

private TabulatedFunction func;
private Object lock;

    public ReadTask(TabulatedFunction func, Object lock) {
        this.func = func;
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            // Читаем значения для всех индексов
            for (int i = 0; i < func.getCount(); i++) {
                synchronized (lock) {
                    double x = func.getX(i);
                    double y = func.getY(i);
                    System.out.printf("After read: i = %d, x = %f, y = %f%n", i, x, y);
                }
                
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
