package ru.ssau.tk._shederu_._lab1_.io;

import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.factory.TabulatedFunctionFactory;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

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

    public static TabulatedFunction deserialize(BufferedInputStream stream) throws IOException, ClassNotFoundException {

        ObjectInputStream objectInputStream = new ObjectInputStream(stream);
        return (TabulatedFunction) objectInputStream.readObject();
    }

    public static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        ObjectOutputStream objectStream = new ObjectOutputStream(stream);
        objectStream.writeObject(function);
        objectStream.flush();
    }

    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory) throws IOException {
        try {
            String line = reader.readLine();
            int count = Integer.parseInt(line);

            double[] xValues = new double[count];
            double[] yValues = new double[count];

            NumberFormat formatter = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            for (int i = 0; i < count; i++) {
                line = reader.readLine();
                String[] parts = line.split(" ");

                xValues[i] = formatter.parse(parts[0]).doubleValue();
                yValues[i] = formatter.parse(parts[1]).doubleValue();
            }

            return factory.create(xValues, yValues);

        } catch (ParseException e) {
            throw new IOException(e);
        }
    }
}