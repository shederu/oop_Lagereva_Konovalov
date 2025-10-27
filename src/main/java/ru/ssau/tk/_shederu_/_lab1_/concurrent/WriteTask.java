package ru.ssau.tk._shederu_._lab1_.concurrent;


import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

public class WriteTask implements Runnable {
    private TabulatedFunction func;
    private final double value;
    private Object lock;

    public WriteTask(TabulatedFunction func, double value, Object lock) {
        this.func = func;
        this.value = value;
        this.lock = lock;
    }
    @Override
    public void run() {
        try {
            for (int i = 0; i < func.getCount(); i++) {
                synchronized (lock) {
                    func.setY(i, value);
                    System.out.printf("Writing for index %d complete%n", i);
                }

                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
