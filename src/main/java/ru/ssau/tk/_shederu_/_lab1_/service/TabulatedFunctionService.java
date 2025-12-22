package ru.ssau.tk._shederu_._lab1_.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.repository.TabulatedFunctionRepository;
import ru.ssau.tk._shederu_._lab1_.util.DerivativeCalculator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TabulatedFunctionService {

    @Autowired
    private TabulatedFunctionRepository repository;

    /**
     * ✅ НОВЫЙ МЕТОД: Создать функцию из массивов X и Y
     */
    public TabulatedFunctionEntity createFromArray(
            List<Double> xValues,
            List<Double> yValues,
            String name,
            UserEntity user) {
        validateInput(xValues, yValues);
        try {
            List<Double> derivativeYValues = DerivativeCalculator.calculateDerivative(xValues, yValues);
            TabulatedFunctionEntity entity = new TabulatedFunctionEntity();
            entity.setName(name != null && !name.trim().isEmpty() ? name : "Функция_" + System.currentTimeMillis());
            entity.setData(serializeDoubleList(yValues));
            entity.setDerivative(serializeDoubleList(derivativeYValues));
            entity.setUser(user);
            return repository.save(entity);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сериализации данных: " + e.getMessage(), e);
        }
    }

    /**
     * Получить функцию по ID с десериализацией данных
     */
    public TabulatedFunctionEntity getById(Long id, UserEntity user) {
        Optional<TabulatedFunctionEntity> optional = repository.findByIdAndUser(id, user);
        return optional.orElse(null);
    }

    /**
     * Получить функцию по ID (для OperationsController)
     */
    public TabulatedFunctionEntity getById(Long id) {
        Optional<TabulatedFunctionEntity> optional = repository.findById(id);
        return optional.orElse(null);
    }

    /**
     * Получить все функции пользователя
     */
    public List<TabulatedFunctionEntity> getAllByUser(UserEntity user) {
        return repository.findByUser(user);
    }

    /**
     * Обновить функцию
     */
    public TabulatedFunctionEntity update(Long id, List<Double> xValues, List<Double> yValues, UserEntity user) {
        validateInput(xValues, yValues);
        Optional<TabulatedFunctionEntity> optional = repository.findByIdAndUser(id, user);
        if (optional.isPresent()) {
            try {
                TabulatedFunctionEntity entity = optional.get();
                List<Double> derivativeYValues = DerivativeCalculator.calculateDerivative(xValues, yValues);
                entity.setData(serializeDoubleList(yValues));
                entity.setDerivative(serializeDoubleList(derivativeYValues));
                return repository.save(entity);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка сериализации данных: " + e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * Удалить функцию
     */
    public void delete(Long id, UserEntity user) {
        Optional<TabulatedFunctionEntity> optional = repository.findByIdAndUser(id, user);
        if (optional.isPresent()) {
            repository.delete(optional.get());
        }
    }

    /**
     * ✅ НОВЫЙ МЕТОД: Загрузить функцию из БД в объект TabulatedFunction
     */
    public TabulatedFunction loadFunctionFromDb(Long id) {
        TabulatedFunctionEntity entity = getById(id);
        if (entity == null) {
            throw new RuntimeException("Функция с id " + id + " не найдена");
        }

        try {
            List<Double> yValues = deserializeDoubleList(entity.getData());

            double[] xArray = new double[yValues.size()];
            double[] yArray = new double[yValues.size()];

            for (int i = 0; i < yValues.size(); i++) {
                xArray[i] = (double) i;
                yArray[i] = yValues.get(i);
            }

            return new ArrayTabulatedFunction(xArray, yArray);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка десериализации функции: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ НОВЫЙ МЕТОД: Сохранить функцию в БД
     */
    public TabulatedFunctionEntity saveFunctionToDb(TabulatedFunction function, String name, Long userId) {
        try {
            double[] xArray = new double[function.getCount()];
            double[] yArray = new double[function.getCount()];

            for (int i = 0; i < function.getCount(); i++) {
                xArray[i] = function.getX(i);
                yArray[i] = function.getY(i);
            }

            List<Double> xValues = new ArrayList<>();
            List<Double> yValues = new ArrayList<>();
            for (double x : xArray) xValues.add(x);
            for (double y : yArray) yValues.add(y);

            List<Double> derivativeYValues = DerivativeCalculator.calculateDerivative(xValues, yValues);

            TabulatedFunctionEntity entity = new TabulatedFunctionEntity();
            entity.setName(name != null && !name.trim().isEmpty() ? name : "Результат_" + System.currentTimeMillis());
            entity.setData(serializeDoubleList(yValues));
            entity.setDerivative(serializeDoubleList(derivativeYValues));
            entity.setUserId(userId);

            return repository.save(entity);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения функции: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ НОВЫЙ МЕТОД: Конвертировать функцию в DTO
     */
    public TabulatedFunctionDto functionToDto(TabulatedFunction function, String name, Long userId) {
        try {
            List<Double> xValues = new ArrayList<>();
            List<Double> yValues = new ArrayList<>();

            for (int i = 0; i < function.getCount(); i++) {
                xValues.add(function.getX(i));
                yValues.add(function.getY(i));
            }

            TabulatedFunctionDto dto = new TabulatedFunctionDto();
            dto.setName(name);
            dto.setXValues(xValues);
            dto.setYValues(yValues);

            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка конвертации функции: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ НОВЫЙ МЕТОД: Создать функцию из математической функции
     */
    public TabulatedFunctionEntity createFromFunction(
            String functionName,
            Double min,
            Double max,
            Integer points,
            UserEntity user) {
        try {
            List<Double> xValues = new ArrayList<>();
            List<Double> yValues = new ArrayList<>();

            double step = (max - min) / (points - 1);
            for (int i = 0; i < points; i++) {
                double x = min + i * step;
                xValues.add(x);
                // Здесь подставь нужную функцию вместо Math.sin
                yValues.add(Math.sin(x));
            }

            return createFromArray(xValues, yValues, functionName, user);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания функции: " + e.getMessage(), e);
        }
    }

    /**
     * Сериализует список Double в byte[]
     */
    private byte[] serializeDoubleList(List<Double> values) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(values.size());
        for (Double value : values) {
            dos.writeDouble(value);
        }
        dos.flush();
        return baos.toByteArray();
    }

    /**
     * Десериализует byte[] обратно в List
     */
    public List<Double> deserializeDoubleList(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return new ArrayList<>();
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        int size = dis.readInt();
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            values.add(dis.readDouble());
        }
        return values;
    }

    /**
     * Валидация входных данных
     */
    private void validateInput(List<Double> xValues, List<Double> yValues) {
        if (xValues == null || yValues == null) {
            throw new IllegalArgumentException("X и Y не должны быть null");
        }
        if (xValues.isEmpty() || yValues.isEmpty()) {
            throw new IllegalArgumentException("X и Y не должны быть пустыми");
        }
        if (xValues.size() != yValues.size()) {
            throw new IllegalArgumentException("Размеры X и Y должны совпадать");
        }
        for (int i = 1; i < xValues.size(); i++) {
            if (xValues.get(i) <= xValues.get(i - 1)) {
                throw new IllegalArgumentException(
                        String.format("X не отсортирован: позиция %d, значение %.2f <= %.2f",
                                i, xValues.get(i), xValues.get(i - 1))
                );
            }
        }
    }
}
