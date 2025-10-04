package ru.ssau.tk._shederu_._lab1_.exceptions;

public class ArrayIsNotSortedException extends RuntimeException{

    public ArrayIsNotSortedException(){
        super();
    }

    public ArrayIsNotSortedException(String message){
        super(message);
    }
}
