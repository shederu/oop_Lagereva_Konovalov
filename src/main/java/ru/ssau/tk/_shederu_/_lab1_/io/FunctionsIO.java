package ru.ssau.tk._shederu_._lab1_.io;

import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;

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
}