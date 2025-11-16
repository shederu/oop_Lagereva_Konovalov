package ru.ssau.tk._shederu_._lab1_.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.operations.TabulatedDifferentialOperator;
import ru.ssau.tk._shederu_._lab1_.functions.factory.LinkedListTabulatedFunctionFactory;
import java.io.*;

public class LinkedListTabulatedFunctionSerialization {
    private static final Logger logger = LoggerFactory.getLogger(LinkedListTabulatedFunctionSerialization.class);
    public static void main(String[] args) {
        logger.info("Запуск сериализации LinkedListTabulatedFunction");

        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction originalFunction = new LinkedListTabulatedFunction(xValues, yValues);

        TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction firstDerivative = differentialOperator.derive(originalFunction);
        TabulatedFunction secondDerivative = differentialOperator.derive(firstDerivative);

        logger.info("Вычислены производные: первая ({} точек), вторая ({} точек)", firstDerivative.getCount(), secondDerivative.getCount());

        try (FileOutputStream fileOutputStream = new FileOutputStream("output/serialized linked list functions.bin");
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {

            FunctionsIO.serialize(bufferedOutputStream, originalFunction);
            FunctionsIO.serialize(bufferedOutputStream, firstDerivative);
            FunctionsIO.serialize(bufferedOutputStream, secondDerivative);

            logger.info("Сериализация завершена");

        } catch (IOException e) {
            logger.error("Ошибка сериализации в файл");
        }

        logger.debug("Десериализация из файла");
        try (FileInputStream fileInputStream = new FileInputStream("output/serialized linked list functions.bin");
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            TabulatedFunction deserializedOriginal = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction deserializedFirstDerivative = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction deserializedSecondDerivative = FunctionsIO.deserialize(bufferedInputStream);

            logger.info("Десериализация завершена");

            System.out.println("\nИсходная функция:");
            System.out.println(deserializedOriginal.toString());

            System.out.println("\nПервая производная:");
            System.out.println(deserializedFirstDerivative.toString());

            System.out.println("\nВторая производная:");
            System.out.println(deserializedSecondDerivative.toString());

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Ошибка десериализации");
        }
        logger.info("Программа сериализации LinkedListTabulatedFunction завершена");
    }
}