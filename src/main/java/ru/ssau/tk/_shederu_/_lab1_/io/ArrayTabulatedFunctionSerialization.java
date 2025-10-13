package ru.ssau.tk._shederu_._lab1_.io;

import ru.ssau.tk._shederu_._lab1_.functions.*;
import ru.ssau.tk._shederu_._lab1_.operations.TabulatedDifferentialOperator;
import java.io.*;

public class ArrayTabulatedFunctionSerialization {
    public static void main(String[] args) {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();

        TabulatedFunction firstDerivative = differentialOperator.derive(function);
        TabulatedFunction secondDerivative = differentialOperator.derive(firstDerivative);

        try (FileOutputStream fileOut = new FileOutputStream("output/serialized array functions.bin");
             BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut)) {

            FunctionsIO.serialize(bufferedOut, function);
            FunctionsIO.serialize(bufferedOut, firstDerivative);
            FunctionsIO.serialize(bufferedOut, secondDerivative);

            System.out.println("Функции успешно сериализованы в файл");

        } catch (IOException e) {
            System.err.println("Ошибка при сериализации:");
            e.printStackTrace();
        }

        try (FileInputStream fileIn = new FileInputStream("output/serialized array functions.bin");
             BufferedInputStream bufferedIn = new BufferedInputStream(fileIn)) {

            TabulatedFunction deserializedFunction = FunctionsIO.deserialize(bufferedIn);
            TabulatedFunction deserializedFirstDerivative = FunctionsIO.deserialize(bufferedIn);
            TabulatedFunction deserializedSecondDerivative = FunctionsIO.deserialize(bufferedIn);

            System.out.println("\nИсходная функция:");
            System.out.println(deserializedFunction.toString());

            System.out.println("\nПервая производная:");
            System.out.println(deserializedFirstDerivative.toString());

            System.out.println("\nВторая производная:");
            System.out.println(deserializedSecondDerivative.toString());

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при десериализации:");
            e.printStackTrace();
        }
    }
}
