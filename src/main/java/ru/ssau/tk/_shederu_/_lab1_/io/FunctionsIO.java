package ru.ssau.tk._shederu_._lab1_.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.factory.TabulatedFunctionFactory;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class FunctionsIO {
    private static final Logger logger = LoggerFactory.getLogger(FunctionsIO.class);

    private FunctionsIO() {
        throw new UnsupportedOperationException("Нельзя создать экземпляр класса FunctionsIO");
    }

    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function) throws IOException {
        logger.debug("Запись функции в текстовый формат: {} точек", function.getCount());

        PrintWriter printWriter = new PrintWriter(writer);
        printWriter.println(function.getCount());

        for (int i = 0; i < function.getCount(); i++) {
            double x = function.getX(i);
            double y = function.getY(i);
            printWriter.printf("%f %f\n", x, y);
            logger.trace("Записана точка {}: x={}, y={}", i, x, y);
        }
        printWriter.flush();
        logger.debug("Запись в текстовый формат завершена");
    }

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function) throws IOException {
        logger.debug("Запись функции в бинарный формат: {} точек", function.getCount());

        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            dataOutputStream.writeInt(function.getCount());
            logger.trace("Записан размер: {}", function.getCount());

            for (int i = 0; i < function.getCount(); i++) {
                dataOutputStream.writeDouble(function.getX(i));
                dataOutputStream.writeDouble(function.getY(i));
                logger.trace("Записана точка");
            }

            dataOutputStream.flush();
            logger.debug("Запись в бинарный формат завершена");
        } catch (IOException e) {
            logger.error("Ошибка записи в бинарный формат", e);
            throw e;
        }
    }

    public static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory) throws IOException {
        logger.debug("Чтение функции из бинарного формата");

        try (DataInputStream dataStream = new DataInputStream(inputStream)) {
            int size = dataStream.readInt();

            if (size < 0) {
                logger.error("Некорректный размер функции");
                throw new IOException("Invalid function size: " + size);
            }

            double[] xValues = new double[size];
            double[] yValues = new double[size];

            for (int i = 0; i < size; i++) {
                xValues[i] = dataStream.readDouble();
                yValues[i] = dataStream.readDouble();
                logger.trace("Прочитана точка {}: x={}, y={}", i, xValues[i], yValues[i]);
            }

            logger.debug("Функция успешно прочитана из бинарного формата");
            return factory.create(xValues, yValues);

        } catch (IOException e) {
            logger.error("Ошибка чтения из бинарного формата", e);
            throw e;
        }
    }

    public static TabulatedFunction deserialize(BufferedInputStream stream) throws IOException, ClassNotFoundException {
        logger.debug("Десериализация функции");

        try (ObjectInputStream objectInputStream = new ObjectInputStream(stream)) {
            TabulatedFunction function = (TabulatedFunction) objectInputStream.readObject();
            logger.debug("Функция десериализована");
            return function;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Ошибка десериализации функции", e);
            throw e;
        }
    }

    public static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        logger.debug("Сериализация функции");

        try (ObjectOutputStream objectStream = new ObjectOutputStream(stream)) {
            objectStream.writeObject(function);
            objectStream.flush();
            logger.debug("Сериализация завершена успешно");
        } catch (IOException e) {
            logger.error("Ошибка сериализации функции", e);
            throw e;
        }
    }

    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory) throws IOException {
        logger.debug("Чтение функции из текстового формата");

        try {
            String line = reader.readLine();

            if (line == null) {
                logger.error("Пустой файл или конец потока");
                throw new IOException("Empty file or end of stream");
            }

            int count = Integer.parseInt(line);

            if (count < 0) {
                logger.error("Некорректный размер функции");
                throw new IOException("Invalid function size: " + count);
            }

            double[] xValues = new double[count];
            double[] yValues = new double[count];

            NumberFormat formatter = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            for (int i = 0; i < count; i++) {
                line = reader.readLine();

                if (line == null) {
                    logger.error("Неожиданный конец файла на точке {}", i);
                    throw new IOException("Unexpected end of file at point " + i);
                }

                String[] parts = line.split(" ");

                if (parts.length < 2) {
                    logger.error("Некорректный формат строки: '{}'", line);
                    throw new IOException("Invalid line format: " + line);
                }

                xValues[i] = formatter.parse(parts[0]).doubleValue();
                yValues[i] = formatter.parse(parts[1]).doubleValue();

                logger.trace("Прочитана точка {}: x={}, y={}", i, xValues[i], yValues[i]);
            }

            logger.debug("Функция успешно прочитана");
            return factory.create(xValues, yValues);

        } catch (ParseException e) {
            logger.error("Ошибка парсинга чисел", e);
            throw new IOException(e);
        } catch (NumberFormatException e) {
            logger.error("Ошибка формата числа", e);
            throw new IOException("Number format error", e);
        }
    }
}