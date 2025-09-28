package ru.ssau.tk._shederu_._lab1_.functions;

interface TabulatedFunction extends MathFunctions {

    int getCount();

    double getX(int index);

    double getY(int index);

    void setY(int index, double value);

    int indexOfX(double x);

    int indexOfY(double y);

    double leftBound();

    double rightBound();

}
