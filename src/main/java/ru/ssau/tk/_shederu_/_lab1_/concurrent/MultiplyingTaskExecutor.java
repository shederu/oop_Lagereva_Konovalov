package ru.ssau.tk._shederu_._lab1_.concurrent;

import ru.ssau.tk._shederu_._lab1_.functions.*;
import java.util.ArrayList;
import java.util.List;

public class MultiplyingTaskExecutor {
    public static void main(String[] args){
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(), 1, 1000, 1000);

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10; i++){
            MultiplyingTask task = new MultiplyingTask(function);
            Thread thread = new Thread(task);
            threads.add(thread);
        }

        for(Thread thread: threads){
            thread.start();
        }

        try{
            Thread.sleep(2000);
        } catch (InterruptedException exc){
            exc.printStackTrace();
        }

        System.out.println("\nПроверка:");
        double expectedValue = Math.pow(2, 10);
        System.out.println("Ожидаемое значение y: " + expectedValue);
        System.out.println("Значение y в первой точке: " + function.getY(0));
    }
}
