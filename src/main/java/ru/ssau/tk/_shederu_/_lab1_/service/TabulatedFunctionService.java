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

    // ================== СОЗДАНИЕ ИЗ ФУНКЦИИ ==================

    /**
     * Создать табулированную функцию по имени функции, интервалу и числу точек.
     */
    public TabulatedFunctionEntity createFromFunction(
            String functionName,
            Double min,
            Double max,
            Integer points,
            UserEntity user
    ) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Пользователь не задан при создании функции");
        }
        if (min == null || max == null || points == null) {
            throw new IllegalArgumentException("min, max и points не должны быть null");
        }
        if (points < 2) {
            throw new IllegalArgumentException("Количество точек должно быть ≥ 2");
        }
        if (max <= min) {
            throw new IllegalArgumentException("max должно быть больше min");
        }

        try {
            List<Double> xValues = new ArrayList<>();
            List<Double> yValues = new ArrayList<>();

            double step = (max - min) / (points - 1);

            for (int i = 0; i < points; i++) {
                double x = min + i * step;
                xValues.add(x);

                double y;
                switch (functionName) {
                    case "cos":
                    case "Косинус":
                        y = Math.cos(x);
                        break;
                    case "x^2":
                    case "Квадрат":
                        y = x * x;
                        break;
                    case "zero":
                    case "Нулевая":
                        y = 0.0;
                        break;
                    case "sin":
                    case "Синус":
                    default:
                        y = Math.sin(x);
                        break;
                }
                yValues.add(y);
            }

            return createFromArray(xValues, yValues, functionName, user);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания функции: " + e.getMessage(), e);
        }
    }

    // ================== СОЗДАНИЕ ИЗ МАССИВОВ ==================

    /**
     * Создать функцию из массивов X и Y для конкретного пользователя.
     */
    public TabulatedFunctionEntity createFromArray(
            List<Double> xValues,
            List<Double> yValues,
            String name,
            UserEntity user
    ) {
        validateInput(xValues, yValues);

        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Пользователь не задан при сохранении функции");
        }

        try {
            List<Double> derivativeYValues =
                    DerivativeCalculator.calculateDerivative(xValues, yValues);

            TabulatedFunctionEntity entity = new TabulatedFunctionEntity();
            entity.setName(
                    name != null && !name.trim().isEmpty()
                            ? name
                            : "Функция_" + System.currentTimeMillis()
            );
            entity.setData(serializeDoubleList(yValues));
            entity.setDerivative(serializeDoubleList(derivativeYValues));
            entity.setUser(user);

            return repository.save(entity);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сериализации данных: " + e.getMessage(), e);
        }
    }

    // ================== ЧТЕНИЕ / ОБНОВЛЕНИЕ / УДАЛЕНИЕ ==================

    public TabulatedFunctionEntity getById(Long id, UserEntity user) {
        Optional<TabulatedFunctionEntity> optional = repository.findByIdAndUser(id, user);
        return optional.orElse(null);
    }

    public TabulatedFunctionEntity getById(Long id) {
        Optional<TabulatedFunctionEntity> optional = repository.findById(id);
        return optional.orElse(null);
    }

    public List<TabulatedFunctionEntity> getAllByUser(UserEntity user) {
        return repository.findByUser(user);
    }

    public TabulatedFunctionEntity update(Long id,
                                          List<Double> xValues,
                                          List<Double> yValues,
                                          UserEntity user) {
        validateInput(xValues, yValues);

        Optional<TabulatedFunctionEntity> optional = repository.findByIdAndUser(id, user);
        if (optional.isPresent()) {
            try {
                TabulatedFunctionEntity entity = optional.get();

                List<Double> derivativeYValues =
                        DerivativeCalculator.calculateDerivative(xValues, yValues);

                entity.setData(serializeDoubleList(yValues));
                entity.setDerivative(serializeDoubleList(derivativeYValues));

                return repository.save(entity);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка сериализации данных: " + e.getMessage(), e);
            }
        }
        return null;
    }

    public void delete(Long id, UserEntity user) {
        Optional<TabulatedFunctionEntity> optional = repository.findByIdAndUser(id, user);
        optional.ifPresent(repository::delete);
    }

    // ================== РАБОТА С TabulatedFunction ==================

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
                xArray[i] = i; // если нужно, можно хранить X отдельно
                yArray[i] = yValues.get(i);
            }

            return new ArrayTabulatedFunction(xArray, yArray);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка десериализации функции: " + e.getMessage(), e);
        }
    }

    public TabulatedFunctionEntity saveFunctionToDb(
            TabulatedFunction function,
            String name,
            UserEntity user
    ) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Пользователь не задан при сохранении функции");
        }

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

            List<Double> derivativeYValues =
                    DerivativeCalculator.calculateDerivative(xValues, yValues);

            TabulatedFunctionEntity entity = new TabulatedFunctionEntity();
            entity.setName(
                    name != null && !name.trim().isEmpty()
                            ? name
                            : "Результат_" + System.currentTimeMillis()
            );
            entity.setData(serializeDoubleList(yValues));
            entity.setDerivative(serializeDoubleList(derivativeYValues));
            entity.setUser(user);

            return repository.save(entity);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения функции: " + e.getMessage(), e);
        }
    }

    public TabulatedFunctionDto functionToDto(
            TabulatedFunction function,
            String name,
            Long userId // можно не использовать, если не нужен в DTO
    ) {
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

    // ================== СЕРИАЛИЗАЦИЯ / ДЕСЕРИАЛИЗАЦИЯ ==================

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

    // ================== ВАЛИДАЦИЯ ==================

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
                        String.format(
                                "X не отсортирован: позиция %d, значение %.2f <= %.2f",
                                i, xValues.get(i), xValues.get(i - 1)
                        )
                );
            }
        }
    }
}
