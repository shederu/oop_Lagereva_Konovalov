package ru.ssau.tk._shederu_._lab1_.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

import java.io.*;

public class TabulatedFunctionFileOutputStream {
    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionFileOutputStream.class);

    public static void main(String[] args) {
        logger.info("Запуск записи функций в бинарные файлы");

        double[] xTest = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yTest = {2.0, 4.0, 6.0, 8.0, 10.0};

        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xTest, yTest);
        TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xTest, yTest);

        logger.info("Созданы функции: ArrayTabulatedFunction, LinkedListTabulatedFunction");

        try (FileOutputStream arrayFileStream = new FileOutputStream("output/array function.bin");
             BufferedOutputStream arrayBufferedStream = new BufferedOutputStream(arrayFileStream);

             FileOutputStream linkedListFileStream = new FileOutputStream("output/linked list function.bin");
             BufferedOutputStream linkedListBufferedStream = new BufferedOutputStream(linkedListFileStream)) {

            FunctionsIO.writeTabulatedFunction(arrayBufferedStream, arrayFunction);
            FunctionsIO.writeTabulatedFunction(linkedListBufferedStream, linkedListFunction);

            logger.info("Бинарные файлы успешно созданы");
            System.out.println("Бинарные файлы созданы");

        } catch (IOException e) {
            logger.error("Ошибка записи в бинарные файлы", e);
        }
        logger.info("Программа записи функций завершена");
    }
}
