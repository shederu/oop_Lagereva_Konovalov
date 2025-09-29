package ru.ssau.tk._shederu_._lab1_.functions;

import java.util.Arrays;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable{
    private  double[] xValues;
    private  double[] yValues;
    private  int count;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues){
        if(xValues.length != yValues.length){
            throw new IllegalArgumentException("Ошибка. Разные длины.");
        }
        if (xValues.length < 2){
            throw new IllegalArgumentException("Длина массива должна быть не меньше двух.");
        }

        for(int i = 0; i < xValues.length-1; i++){
            for (int j = i+1; j < xValues.length; j++){
                if(Math.abs(xValues[i]-xValues[j]) < 1e-10){
                    throw new IllegalArgumentException("Элементы не должны повторяться.");
                }
            }
        }

        for(int i = 1; i < xValues.length; i++){
            if (xValues[i-1] > xValues[i]){
                throw new IllegalArgumentException("Значения должны быть упорядочены.");
            }
        }

        this.count = xValues.length;
        this.xValues = Arrays.copyOf(xValues, count);
        this.yValues = Arrays.copyOf(yValues, count);
    }

    public ArrayTabulatedFunction(MathFunctions source, double xFrom, double xTo, int count){
        this.xValues = new double[count];
        this.yValues = new double[count];
        this.count = count;

        if (count < 2){
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
    }


    @Override
    public int getCount(){
        return count;
    }

    @Override
    public double getX(int index){
        if (index < 0 || index >= count){
            throw new IllegalArgumentException("Индекс выходит за пределы.");
        }
        return xValues[index];
    }

    @Override
    public double getY(int index){
        if(index < 0 || index >= count){
            throw new IllegalArgumentException("Индекс выходит за пределы.");
        }
        return yValues[index];
    }

    @Override
    public void setY(int index, double value){
        if(index < 0 || index >= count){
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

        int existingIndex = indexOfX(x);
        if (existingIndex != -1) {
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
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Не существует элемента с данным индексом");
        }

        if (count <= 2) {
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
    }

    @Test
    public void testInsert() {
        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {2.0, 6.0, 10.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.insert(0.0, 1.0);
        assertEquals(4, function.getCount());
        assertEquals(0.0, function.getX(0));
        assertEquals(1.0, function.getY(0));

        function.insert(2.0, 4.0);
        assertEquals(5, function.getCount());
        assertEquals(2.0, function.getX(2));
        assertEquals(4.0, function.getY(2));

        function.insert(6.0, 12.0);
        assertEquals(6, function.getCount());
        assertEquals(6.0, function.getX(5));
        assertEquals(12.0, function.getY(5));

        function.insert(2.0, 8.0);
        assertEquals(6, function.getCount());
        assertEquals(8.0, function.getY(2));
    }

}
