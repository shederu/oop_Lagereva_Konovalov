package ru.ssau.tk._shederu_._lab1_.concurrent;

import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

public class ReadTask implements Runnable {

    private TabulatedFunction func;

    public ReadTask(TabulatedFunction func) {
        this.func = func;
    }

    @Override
    public void run() {
        int j = func.getCount();
        for (int i = 0; i < j; i++) {
            System.out.printf("After read: i = %d, x = %f, y = %f%n", i, func.getX(i), func.getY(i));
        }
    }
}
