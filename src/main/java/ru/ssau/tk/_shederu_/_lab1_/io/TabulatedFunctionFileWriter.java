package ru.ssau.tk._shederu_._lab1_.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.functions.*;
import java.io.*;

public class TabulatedFunctionFileWriter {
    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionFileWriter.class);

    public static void main(String[] args) {
        logger.info("Запуск записи функций в текстовые файлы");

        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);

        logger.info("Созданы функции: ArrayTabulatedFunction, LinkedListTabulatedFunction");

        try (FileWriter fileWriter1 = new FileWriter("output/array_function.txt");
             FileWriter fileWriter2 = new FileWriter("output/linked_list_function.txt");
             BufferedWriter bufferedWriter1 = new BufferedWriter(fileWriter1);
             BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter2)) {

            FunctionsIO.writeTabulatedFunction(bufferedWriter1, arrayFunction);
            FunctionsIO.writeTabulatedFunction(bufferedWriter2, linkedListFunction);

            logger.info("Текстовые файлы успешно созданы");
        }
        catch (IOException e) {
            logger.error("Ошибка записи в текстовые файлы", e);
        }
        logger.info("Программа записи в текстовые файлы завершена");
    }
}
