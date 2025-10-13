package ru.ssau.tk._shederu_._lab1_.io;

import ru.ssau.tk._shederu_._lab1_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

import java.io.*;

public class TabulatedFunctionFileOutputStream {
    public static void main(String[] args) {

        double[] xTest = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yTest = {2.0, 4.0, 6.0, 8.0, 10.0};

        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xTest, yTest);
        TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xTest, yTest);

        try (FileOutputStream arrayFileStream = new FileOutputStream("output/array function.bin");
             BufferedOutputStream arrayBufferedStream = new BufferedOutputStream(arrayFileStream);

             FileOutputStream linkedListFileStream = new FileOutputStream("output/linked list function.bin");
             BufferedOutputStream linkedListBufferedStream = new BufferedOutputStream(linkedListFileStream)) {

            FunctionsIO.writeTabulatedFunction(arrayBufferedStream, arrayFunction);
            FunctionsIO.writeTabulatedFunction(linkedListBufferedStream, linkedListFunction);

            System.out.println("Бинарные файлы созданы");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
