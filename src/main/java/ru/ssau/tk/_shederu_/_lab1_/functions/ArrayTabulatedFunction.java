package ru.ssau.tk._shederu_._lab1_.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk._shederu_._lab1_.exceptions.NoSuchElementException;

import java.util.Arrays;
import java.util.Iterator;
import java.io.Serializable;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable, TabulatedFunction{
    private static final Logger logger = LoggerFactory.getLogger(ArrayTabulatedFunction.class);
    private  double[] xValues;
    private  double[] yValues;
    private  int count;

    private static final long serialVersionUID = 1L;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues){
        logger.info("Создание ArrayTabulatedFunction: {} точек", xValues.length);

        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);

        if (xValues.length < 2){
            logger.error("Недостаточно точек: {}", xValues.length);
            throw new IllegalArgumentException("Длина массива должна быть не меньше двух.");
        }

        for(int i = 0; i < xValues.length-1; i++){
            for (int j = i+1; j < xValues.length; j++){
                if(Math.abs(xValues[i]-xValues[j]) < 1e-10){
                    logger.error("Обнаружены дубликаты X");
                    throw new IllegalArgumentException("Элементы не должны повторяться.");
                }
            }
        }

        for(int i = 1; i < xValues.length; i++){
            if (xValues[i-1] > xValues[i]){
                logger.error("Массив не отсортирован");
                throw new ArrayIsNotSortedException("Значения должны быть упорядочены.");
            }
        }

        this.count = xValues.length;
        this.xValues = Arrays.copyOf(xValues, count);
        this.yValues = Arrays.copyOf(yValues, count);
        logger.info("ArrayTabulatedFunction создан: {} точек, диапазон=[{}, {}]", count, leftBound(), rightBound());
    }

    public ArrayTabulatedFunction(MathFunctions source, double xFrom, double xTo, int count){
        logger.debug("Создание ArrayTabulatedFunction из функции: source={}, xFrom={}, xTo={}, count={}", source.getClass().getSimpleName(), xFrom, xTo, count);

        this.xValues = new double[count];
        this.yValues = new double[count];
        this.count = count;

        if (count < 2){
            logger.error("Недостаточно точек");
            throw new IllegalArgumentException("Количество точек должно быть не менее 2.");
        }

        if (xFrom > xTo){
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        if (xFrom == xTo){
            Arrays.fill(xValues, xFrom);
            Arrays.fill(yValues, source.apply(xFrom));
        }
        else{
            double step = (xTo - xFrom)/(count - 1);
            for(int i = 0; i < count; i++){
                xValues[i] = xFrom + i*step;
                yValues[i] = source.apply(xValues[i]);
            }
        }
        logger.info("ArrayTabulatedFunction создан: {} точек, диапазон=[{}, {}], функция={}", count, leftBound(), rightBound(), source.getClass().getSimpleName());
    }


    @Override
    public int getCount(){
        return count;
    }

    @Override
    public double getX(int index){
        if (index < 0 || index >= count){
            logger.error("Некорректный индекс в getX: {}", index);
            throw new IllegalArgumentException("Индекс выходит за пределы.");
        }
        return xValues[index];
    }

    @Override
    public double getY(int index){
        if(index < 0 || index >= count){
            logger.error("Некорректный индекс в getY: {}", index);
            throw new IllegalArgumentException("Индекс выходит за пределы.");
        }
        return yValues[index];
    }

    @Override
    public void setY(int index, double value){
        logger.debug("setY({}, {} -> {})", index, yValues[index], value);
        if(index < 0 || index >= count){
            logger.error("Некорректный индекс в setY: {}", index);
            throw new IllegalArgumentException("Индекс выходит за пределы.");
        }
        yValues[index] = value;
    }

    @Override
    public int indexOfX(double value){
        for(int i = 0; i < count; i++){
            if (Math.abs(xValues[i]-value) < 1e-10) return i;
        }
        return -1;
    }

    @Override
    public int indexOfY(double value){
        for(int i = 0; i < count; i++){
            if (Math.abs(yValues[i]-value) < 1e-10) return i;
        }
        return -1;
    }

    @Override
    public double leftBound(){
        return xValues[0];
    }

    @Override
    public double rightBound(){
        return xValues[count-1];
    }

    @Override
    protected int floorIndexOfX(double x){
        if (x < xValues[0]) return 0;
        if (x > xValues[count-1]) return count-1;
        for (int i = 0; i < count; i++){
            if (x < xValues[i]) return i-1;
        }
        return count - 1;
    }

    @Override
    protected double extrapolateLeft(double x){
        if (count == 1) return yValues[0];
        return interpolate(x, xValues[0], xValues[1], yValues[0], yValues[1]);
    }

    @Override
    protected double extrapolateRight(double x){
        if (count == 1) return yValues[0];
        return interpolate(x, xValues[count - 2], xValues[count - 1], yValues[count - 2], yValues[count - 1]);
    }

    @Override
    protected double interpolate(double x, int floorIndex){
        if (count == 1){
            return yValues[0];
        }

        if(floorIndex < 0 || floorIndex >= count -1){
            throw new IllegalArgumentException("Некорректный floorIndex");
        }

        return interpolate(x, xValues[floorIndex], xValues[floorIndex+1], yValues[floorIndex], yValues[floorIndex+1]);
    }

    @Override
    public void insert(double x, double y) {
        logger.debug("insert(x={}, y={})", x, y);

        int existingIndex = indexOfX(x);
        if (existingIndex != -1) {
            logger.debug("Обновление существующей точки: index={}, y={} -> {}", existingIndex, yValues[existingIndex], y);
            yValues[existingIndex] = y;
            return;
        }

        int insertIndex = 0;
        while (insertIndex < count && xValues[insertIndex] < x) {
            insertIndex++;
        }

        double[] newXValues = new double[count + 1];
        double[] newYValues = new double[count + 1];

        System.arraycopy(xValues, 0, newXValues, 0, insertIndex);
        System.arraycopy(yValues, 0, newYValues, 0, insertIndex);

        newXValues[insertIndex] = x;
        newYValues[insertIndex] = y;

        System.arraycopy(xValues, insertIndex, newXValues, insertIndex + 1, count - insertIndex);
        System.arraycopy(yValues, insertIndex, newYValues, insertIndex + 1, count - insertIndex);

        xValues = newXValues;
        yValues = newYValues;
        count++;
        logger.info("Точка вставлена: x={}, y={}, всего точек: {}", x, y, count);
    }

    @Override
    public void remove(int index) {
        logger.debug("remove({}) - x={}, y={}", index, xValues[index], yValues[index]);

        if (index < 0 || index >= count) {
            logger.error("Некорректный индекс для удаления: {}", index);
            throw new IllegalArgumentException("Не существует элемента с данным индексом");
        }

        if (count <= 2) {
            logger.error("Попытка удаления при минимальном количестве точек: {}", count);
            throw new IllegalStateException("В таблице должно остаться минимум 2 точки.");
        }

        double[] newXValues = new double[count - 1];
        double[] newYValues = new double[count - 1];

        System.arraycopy(xValues, 0, newXValues, 0, index);
        System.arraycopy(yValues, 0, newYValues, 0, index);

        System.arraycopy(xValues, index + 1, newXValues, index, count - index - 1);
        System.arraycopy(yValues, index + 1, newYValues, index, count - index - 1);

        xValues = newXValues;
        yValues = newYValues;
        count--;
        logger.info("Точка удалена, осталось точек: {}", count);
    }

    @Override
    public Iterator<Point> iterator(){
        return new Iterator<Point>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < count;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    logger.warn("Попытка вызова next() у исчерпанного итератора");
                    throw new NoSuchElementException("Элементы закончились");
                }
                Point point = new Point(xValues[index], yValues[index]);
                index++;
                return point;
            }
        };
    }
}
