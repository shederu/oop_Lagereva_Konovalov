package ru.ssau.tk._shederu_._lab1_.exceptions;

public class NoSuchElementException extends RuntimeException {
    public NoSuchElementException(){
        super();
    }
    public NoSuchElementException(String message) {
        super(message);
    }
}
