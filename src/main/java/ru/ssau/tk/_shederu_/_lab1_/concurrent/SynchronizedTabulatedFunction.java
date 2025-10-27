package ru.ssau.tk._shederu_._lab1_.concurrent;

import ru.ssau.tk._shederu_._lab1_.exceptions.NoSuchElementException;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.Point;
import java.util.Iterator;

public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private final TabulatedFunction function;

    public SynchronizedTabulatedFunction(TabulatedFunction function){
        if (function == null){
            throw new NoSuchElementException("Функция не может быть пустой");
        }
        this.function = function;
    }

    @Override
    public synchronized double apply(double x) {
        return function.apply(x);
    }

    @Override
    public synchronized int getCount(){
        return function.getCount();
    }

    @Override
    public synchronized double getX(int index){
        return function.getX(index);
    }

    @Override
    public synchronized double getY(int index){
        return function.getY(index);
    }

    @Override
    public synchronized void setY(int index, double value){
        function.setY(index, value);
    }

    @Override
    public synchronized int indexOfX(double x){
        return function.indexOfX(x);
    }

    @Override
    public synchronized int indexOfY(double y){
        return function.indexOfY(y);
    }

    @Override
    public synchronized double leftBound(){
        return function.leftBound();
    }

    @Override
    public synchronized double rightBound(){
        return function.rightBound();
    }

    @Override
    public synchronized Iterator<Point> iterator() {
        Point[] points = new Point[function.getCount()];
        for (int i = 0; i < function.getCount(); i++) {
            points[i] = new Point(function.getX(i), function.getY(i));
        }
        return java.util.Arrays.asList(points).iterator();
    }

    public synchronized <T> T doSynchronously(Operation<T> operation) {
        return operation.apply(function);
    }

    @FunctionalInterface
    public interface Operation<T> {
        T apply(TabulatedFunction function);
    }
}
