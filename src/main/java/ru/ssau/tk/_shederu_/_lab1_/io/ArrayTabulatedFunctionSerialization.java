package ru.ssau.tk._shederu_._lab1_.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.functions.*;
import ru.ssau.tk._shederu_._lab1_.operations.TabulatedDifferentialOperator;
import java.io.*;

public class ArrayTabulatedFunctionSerialization {
    private static final Logger logger = LoggerFactory.getLogger(ArrayTabulatedFunctionSerialization.class);

    public static void main(String[] args) {
        logger.info("Запуск сериализации функций");
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        logger.info("Создана исходная функция");

        TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();

        TabulatedFunction firstDerivative = differentialOperator.derive(function);
        TabulatedFunction secondDerivative = differentialOperator.derive(firstDerivative);

        logger.info("Вычислены производные: первая ({} точек), вторая ({} точек)", firstDerivative.getCount(), secondDerivative.getCount());

        String filename = "output/serialized array functions.bin";
        logger.debug("Начало сериализации в файл: {}", filename);

        try (FileOutputStream fileOut = new FileOutputStream(filename);
             BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut)) {

            FunctionsIO.serialize(bufferedOut, function);
            FunctionsIO.serialize(bufferedOut, firstDerivative);
            FunctionsIO.serialize(bufferedOut, secondDerivative);

            logger.info("Сериализация завершена: 3 функции записаны в {}", filename);

        } catch (IOException e) {
            logger.error("Ошибка при сериализации в файл: {}", filename, e);
            return;
        }

        logger.debug("Начало десериализации из файла: {}", filename);

        try (FileInputStream fileIn = new FileInputStream("output/serialized array functions.bin");
             BufferedInputStream bufferedIn = new BufferedInputStream(fileIn)) {

            TabulatedFunction deserializedFunction = FunctionsIO.deserialize(bufferedIn);
            TabulatedFunction deserializedFirstDerivative = FunctionsIO.deserialize(bufferedIn);
            TabulatedFunction deserializedSecondDerivative = FunctionsIO.deserialize(bufferedIn);

            logger.info("Десериализация завершена: 3 функции прочитаны из {}", filename);

            logger.debug("Проверка корректности десериализации");
            if (deserializedFunction.getCount() == function.getCount() &&
                    deserializedFirstDerivative.getCount() == firstDerivative.getCount() &&
                    deserializedSecondDerivative.getCount() == secondDerivative.getCount()) {
                logger.info("Десериализация прошла успешно - размеры функций совпадают");
            } else {
                logger.warn("Размеры десериализованных функций не совпадают с оригиналомб");
            }

            System.out.println("\nИсходная функция:");
            System.out.println(deserializedFunction.toString());

            System.out.println("\nПервая производная:");
            System.out.println(deserializedFirstDerivative.toString());

            System.out.println("\nВторая производная:");
            System.out.println(deserializedSecondDerivative.toString());

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Ошибка при десериализации из файла: {}", filename, e);
        }

        logger.info("Программа сериализации завершена");
    }
}
