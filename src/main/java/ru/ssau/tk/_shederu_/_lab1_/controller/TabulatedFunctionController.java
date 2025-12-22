package ru.ssau.tk._shederu_._lab1_.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.service.TabulatedFunctionService;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tabulatedFunctions")
@CrossOrigin(origins = "http://localhost:3000")
public class TabulatedFunctionController {
    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionController.class);

    @Autowired
    private TabulatedFunctionService tabulatedFunctionService;

    @Autowired
    private UserRepository userRepository;

    /**
     * ✅ Создать функцию из массивов X, Y
     * Проверки:
     * - Name не пусто и не null
     * - Массивы не пусты
     * - Минимум 2 точки
     * - Размеры совпадают
     * - X числовые значения
     * - Y числовые значения
     * - X отсортированы по возрастанию
     * - Уникальное название функции
     */
    @PostMapping("/createFromArray")
    public ResponseEntity<?> createFromArray(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            UserEntity user = userRepository.findByLogin(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<?> xRaw = (List<?>) request.get("x");
            List<?> yRaw = (List<?>) request.get("y");
            String name = (String) request.get("name");

            // ✅ Проверка: name не должно быть пусто или null
            if (name == null || name.trim().isEmpty()) {
                logger.warn("Function name is empty or null");
                return ResponseEntity.badRequest().body("Error: Название функции не должно быть пусто");
            }

            // ✅ Проверка: массивы не должны быть пусты
            if (xRaw == null || yRaw == null || xRaw.isEmpty() || yRaw.isEmpty()) {
                logger.warn("Empty arrays provided");
                return ResponseEntity.badRequest().body("Error: X и Y массивы не должны быть пусты");
            }

            // ✅ Проверка: должно быть не менее 2 точек
            if (xRaw.size() < 2 || yRaw.size() < 2) {
                logger.warn("Insufficient points: x.size={}, y.size={}", xRaw.size(), yRaw.size());
                return ResponseEntity.badRequest().body("Error: Должно быть не менее 2 точек");
            }

            // ✅ Проверка: размеры массивов должны совпадать
            if (xRaw.size() != yRaw.size()) {
                logger.warn("Array sizes mismatch: x.size={}, y.size={}", xRaw.size(), yRaw.size());
                return ResponseEntity.badRequest().body("Error: Размеры массивов X и Y должны совпадать");
            }

            // Конвертируем в List<Double>
            List<Double> xValues = new ArrayList<>();
            List<Double> yValues = new ArrayList<>();

            for (Object xVal : xRaw) {
                if (!(xVal instanceof Number)) {
                    logger.error("Invalid X value type: {}", xVal.getClass());
                    return ResponseEntity.badRequest().body("Error: X должны быть числовыми значениями");
                }
                xValues.add(((Number) xVal).doubleValue());
            }

            for (Object yVal : yRaw) {
                if (!(yVal instanceof Number)) {
                    logger.error("Invalid Y value type: {}", yVal.getClass());
                    return ResponseEntity.badRequest().body("Error: Y должны быть числовыми значениями");
                }
                yValues.add(((Number) yVal).doubleValue());
            }

            // ✅ Проверка: X должны быть отсортированы
            for (int i = 1; i < xValues.size(); i++) {
                if (xValues.get(i) <= xValues.get(i - 1)) {
                    logger.warn("X values not sorted at index {}: {} <= {}", i, xValues.get(i), xValues.get(i - 1));
                    return ResponseEntity.badRequest().body("Error: X значения должны быть отсортированы по возрастанию");
                }
            }

            // ✅ Проверка: функция с таким названием уже существует
            List<TabulatedFunctionEntity> existingFunctions = tabulatedFunctionService.getAllByUser(user);
            boolean nameExists = existingFunctions.stream()
                    .anyMatch(f -> f.getName() != null && f.getName().equals(name));

            if (nameExists) {
                logger.warn("Function with name '{}' already exists for user {}", name, username);
                return ResponseEntity.badRequest().body("Error: Функция с таким названием уже существует");
            }

            logger.debug("Creating function: x={}, y={}, name={}", xValues, yValues, name);

            // ✅ Правильный вызов с 4 параметрами
            TabulatedFunctionEntity entity = tabulatedFunctionService.createFromArray(
                    xValues, yValues, name, user);

            logger.info("Function created: id={}", entity.getId());
            return ResponseEntity.ok(convertToDto(entity));

        } catch (Exception e) {
            logger.error("Error creating function from array", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * ✅ Создать функцию из математической функции
     * Проверки:
     * - Название функции не пусто
     * - Уникальное название функции
     * - Points целое число (не дробь)
     * - Points >= 2
     * - max > min
     */
    @PostMapping("/create-from-function")
    public ResponseEntity<?> createFromFunction(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            UserEntity user = userRepository.findByLogin(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String functionName = (String) request.get("functionName");

            // ✅ Проверка: functionName не должно быть пусто
            if (functionName == null || functionName.trim().isEmpty()) {
                logger.warn("Function name is empty");
                return ResponseEntity.badRequest().body("Error: Название функции не должно быть пусто");
            }

            // ✅ Проверка: функция с таким названием уже существует
            List<TabulatedFunctionEntity> existingFunctions = tabulatedFunctionService.getAllByUser(user);
            boolean nameExists = existingFunctions.stream()
                    .anyMatch(f -> f.getName() != null && f.getName().equals(functionName));

            if (nameExists) {
                logger.warn("Function with name '{}' already exists for user {}", functionName, username);
                return ResponseEntity.badRequest().body("Error: Функция с таким названием уже существует");
            }

            double min = ((Number) request.get("min")).doubleValue();
            double max = ((Number) request.get("max")).doubleValue();

            // ✅ Проверка: points должно быть целым числом >= 2
            Object pointsObj = request.get("points");
            if (pointsObj == null) {
                logger.warn("Points is null");
                return ResponseEntity.badRequest().body("Error: Количество точек не указано");
            }

            int points;
            try {
                double pointsDouble = ((Number) pointsObj).doubleValue();
                // Проверка на дробь
                if (pointsDouble != (int) pointsDouble) {
                    logger.warn("Points is not an integer: {}", pointsDouble);
                    return ResponseEntity.badRequest().body("Error: Количество точек должно быть целым числом");
                }
                points = (int) pointsDouble;
            } catch (Exception e) {
                logger.error("Invalid points format", e);
                return ResponseEntity.badRequest().body("Error: Количество точек должно быть числом");
            }

            // ✅ Проверка: должно быть не менее 2 точек
            if (points < 2) {
                logger.warn("Insufficient points: {}", points);
                return ResponseEntity.badRequest().body("Error: Должно быть не менее 2 точек");
            }

            // ✅ Проверка: max > min
            if (max <= min) {
                logger.warn("Invalid range: max={}, min={}", max, min);
                return ResponseEntity.badRequest().body("Error: Максимальное значение должно быть больше минимального");
            }

            logger.debug("Creating from function: name={}, min={}, max={}, points={}",
                    functionName, min, max, points);

            // ✅ Правильный вызов с 5 параметрами
            TabulatedFunctionEntity entity = tabulatedFunctionService.createFromFunction(
                    functionName, min, max, points, user);

            logger.info("Function created: id={}", entity.getId());
            return ResponseEntity.ok(convertToDto(entity));

        } catch (NumberFormatException e) {
            logger.error("Invalid number format", e);
            return ResponseEntity.badRequest().body("Error: Неверный формат числа");
        } catch (Exception e) {
            logger.error("Error creating function from function", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * ✅ Получить все функции пользователя
     */
    @GetMapping
    public ResponseEntity<?> getAllFunctions(Authentication authentication) {
        try {
            String username = authentication.getName();
            UserEntity user = userRepository.findByLogin(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            logger.debug("Fetching functions for user: {}", username);

            // ✅ Правильный вызов с UserEntity
            List<TabulatedFunctionEntity> entities = tabulatedFunctionService.getAllByUser(user);
            List<TabulatedFunctionDto> dtos = entities.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            logger.info("Retrieved {} functions", dtos.size());
            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            logger.error("Error fetching functions", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * ✅ Получить функцию по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFunctionById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            UserEntity user = userRepository.findByLogin(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            logger.debug("Fetching function with id: {}", id);

            // ✅ Правильный вызов с id и user
            TabulatedFunctionEntity entity = tabulatedFunctionService.getById(id, user);
            if (entity == null) {
                return ResponseEntity.notFound().build();
            }

            TabulatedFunctionDto dto = convertToDto(entity);
            logger.info("Retrieved function: id={}", id);
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            logger.error("Error fetching function", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * ✅ Удалить функцию
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFunction(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            UserEntity user = userRepository.findByLogin(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            logger.debug("Deleting function with id: {}", id);

            // ✅ Правильный вызов с id и user
            tabulatedFunctionService.delete(id, user);
            logger.info("Function deleted: id={}", id);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Error deleting function", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * ✅ Получить точки функции для графика
     */
    @GetMapping("/{id}/points")
    public ResponseEntity<?> getFunctionPoints(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            UserEntity user = userRepository.findByLogin(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            logger.debug("Fetching points for function: {}", id);

            TabulatedFunctionEntity entity = tabulatedFunctionService.getById(id, user);
            if (entity == null) {
                return ResponseEntity.notFound().build();
            }

            // ✅ Десериализуем данные из entity
            List<Double> yValues = tabulatedFunctionService.deserializeDoubleList(entity.getData());
            List<Double> xValues = new ArrayList<>();
            for (int i = 0; i < yValues.size(); i++) {
                xValues.add((double) i);
            }

            Map<String, Object> points = Map.of(
                    "x", xValues,
                    "y", yValues
            );

            return ResponseEntity.ok(points);

        } catch (Exception e) {
            logger.error("Error fetching points", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * ✅ Вспомогательный метод: конвертировать Entity -> DTO
     */
    private TabulatedFunctionDto convertToDto(TabulatedFunctionEntity entity) {
        try {
            TabulatedFunctionDto dto = new TabulatedFunctionDto();
            dto.setId(entity.getId());
            dto.setName(entity.getName());

            // Десериализуем Y значения из entity
            List<Double> yValues = tabulatedFunctionService.deserializeDoubleList(entity.getData());

            // X значения — это индексы (0, 1, 2, ...)
            List<Double> xValues = new ArrayList<>();
            for (int i = 0; i < yValues.size(); i++) {
                xValues.add((double) i);
            }

            dto.setXValues(xValues);
            dto.setYValues(yValues);

            // Если есть производная, десериализуем и её
            if (entity.getDerivative() != null) {
                dto.setDerivativeYValues(tabulatedFunctionService.deserializeDoubleList(entity.getDerivative()));
            }

            return dto;
        } catch (Exception e) {
            logger.error("Error converting entity to DTO", e);
            throw new RuntimeException("Error converting data: " + e.getMessage(), e);
        }
    }
}
