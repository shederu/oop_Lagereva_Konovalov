package ru.ssau.tk._shederu_._lab1_.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.exceptions.NoSuchElementException;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.Point;
import java.util.Iterator;

public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedTabulatedFunction.class);
    private final TabulatedFunction function;

    public SynchronizedTabulatedFunction(TabulatedFunction function){
        if (function == null){
            logger.error("Попытка создания SynchronizedTabulatedFunction с null функцией");
            throw new NoSuchElementException("Функция не может быть пустой");
        }
        this.function = function;
        logger.debug("SynchronizedTabulatedFunction создан");
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
        logger.debug("iterator() - создание синхронизированного итератора");

        Point[] pointsCopy = new Point[function.getCount()];
        for (int i = 0; i < function.getCount(); i++) {
            pointsCopy[i] = new Point(function.getX(i), function.getY(i));
        }

        logger.trace("Создана копия {} точек для итератора", pointsCopy.length);

        return new Iterator<Point>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < pointsCopy.length;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    logger.warn("Попытка вызова next() у исчерпанного итератора");
                    throw new NoSuchElementException();
                }
                Point point = pointsCopy[currentIndex];
                logger.trace("iterator.next() = Point({}, {})", point.x, point.y);
                currentIndex++;
                return point;
            }

            @Override
            public void remove() {
                logger.warn("Попытка вызова remove() у итератора");
                throw new UnsupportedOperationException("Remove operation is not supported");
            }
        };
    }

    public synchronized <T> T doSynchronously(Operation<T> operation) {
        logger.debug("doSynchronously() - выполнение операции с блокировкой");
        try {
            T result = operation.apply(this);
            logger.debug("doSynchronously() - операция выполнена успешно");
            return result;
        } catch (Exception e) {
            logger.error("doSynchronously() - ошибка при выполнении операции", e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface Operation<T> {
        T apply(SynchronizedTabulatedFunction function);
    }
}
