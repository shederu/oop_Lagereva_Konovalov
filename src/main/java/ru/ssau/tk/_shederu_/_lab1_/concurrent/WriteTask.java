package ru.ssau.tk._shederu_._lab1_.concurrent;

import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

public class WriteTask implements Runnable {
    private  TabulatedFunction func;
    private final double value;

    public WriteTask(TabulatedFunction func, double value) {
        this.func = func;
        this.value = value;
    }

    @Override
    public void run() {
        int count = func.getCount();
        for (int i = 0; i < count; i++) {
            func.setY(i, value);
            System.out.printf("Writing for index %d complete", i);
        }
    }
}
