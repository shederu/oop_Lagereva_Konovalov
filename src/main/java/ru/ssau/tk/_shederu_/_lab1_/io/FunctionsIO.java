package ru.ssau.tk._shederu_._lab1_.io;

import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.factory.TabulatedFunctionFactory;

import java.io.*;

public class FunctionsIO {
    private FunctionsIO() {
        throw new UnsupportedOperationException("Нельзя создать экземпляр класса FunctionsIO");
    }

    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function) throws IOException {
        PrintWriter printWriter = new PrintWriter(writer);

        printWriter.println(function.getCount());

        for (int i = 0; i < function.getCount(); i++) {
            double x = function.getX(i);
            double y = function.getY(i);
            printWriter.printf("%f %f\n", x, y);
        }
        printWriter.flush();
    }

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function) throws IOException {
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            dataOutputStream.writeInt(function.getCount());

            for (int i = 0; i < function.getCount(); i++) {
                dataOutputStream.writeDouble(function.getX(i));
                dataOutputStream.writeDouble(function.getY(i));
            }

            dataOutputStream.flush();
        }
    }

    public static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory) throws IOException {

        DataInputStream dataStream = new DataInputStream(inputStream);

        int size = dataStream.readInt();

        double[] xValues = new double[size];
        double[] yValues = new double[size];

        for (int i = 0; i < size; i++) {
            xValues[i] = dataStream.readDouble();
            yValues[i] = dataStream.readDouble();
        }

        return factory.create(xValues, yValues);
    }

}