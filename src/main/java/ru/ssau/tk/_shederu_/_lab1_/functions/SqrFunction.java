package ru.ssau.tk._shederu_._lab1_.functions;

public class SqrFunction implements MathFunctions {
    @Override   //указывает компилятору, что метод переопределяет метод из родительского класса или интерфейса. Можно и без него, но лучше с ним
    public double apply(double x){
        return Math.pow(x, 2);
    }
}