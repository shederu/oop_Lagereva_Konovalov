package ru.ssau.tk._shederu_._lab1_.functions;

public class MockTabulatedFunction extends AbstractTabulatedFunction{
    private final double x0;
    private final double x1;
    private double y0;
    private double y1;

    public MockTabulatedFunction(double x0, double x1, double y0, double y1){
        if (x0 >= x1){
            throw new IllegalArgumentException("x0 должен быть меньше x1.");
        }
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }

    protected int floorIndexOfX(double x){
        if (x < x0) {
            return 0;
        }
        else if(x <x1){
            return 0;
        }
        return 1;
    }

    protected double extrapolateLeft(double x){
        return interpolate(x, x0, x1, y0, y1);
    }

    protected double extrapolateRight(double x){
        return interpolate(x, x0, x1, y0, y1);
    }

    protected double interpolate(double x, int floorIndex){
        return interpolate(x, x0, x1, y0, y1);
    }

    public int getCount(){
        return 2;
    }

    public double getX(int index){
        return index == 0? x0: x1;
    }

    public double getY(int index){
        return index == 0? y0:y1;
    }

    public void setY(int index, double y){
        if (index == 0){
            y0 = y;
        }
        else if (index == 1){
            y1 = y;
        }
    }

    public double leftBound(){
        return x0;
    }

    public double rightBound(){
        return x1;
    }

    public int indexOfX(double x){
        if (x == x0){
            return 0;
        }
        else if(x == x1){
            return 1;
        }
        return -1;
    }

    public int indexOfY(double y){
        if (y == y0){
            return 0;
        }
        else if(y == y1){
            return 1;
        }
        return -1;
    }
}
