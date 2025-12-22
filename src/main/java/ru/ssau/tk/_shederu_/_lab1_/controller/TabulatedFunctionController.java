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
     * ✅ ИСПРАВЛЕНО: Метод теперь принимает 3 аргумента как в Service
     * Создать функцию из массивов X, Y
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

            // Конвертируем в List<Double>
            List<Double> xValues = xRaw.stream()
                    .map(v -> ((Number) v).doubleValue())
                    .collect(Collectors.toList());

            List<Double> yValues = yRaw.stream()
                    .map(v -> ((Number) v).doubleValue())
                    .collect(Collectors.toList());

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
     * ✅ ИСПРАВЛЕНО: Конвертируем параметры через parseDouble/parseInt
     * Создать функцию из математической функции
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
            double min = ((Number) request.get("min")).doubleValue();
            double max = ((Number) request.get("max")).doubleValue();
            int points = ((Number) request.get("points")).intValue();

            logger.debug("Creating from function: name={}, min={}, max={}, points={}",
                    functionName, min, max, points);

            // ✅ Правильный вызов с 5 параметрами
            TabulatedFunctionEntity entity = tabulatedFunctionService.createFromFunction(
                    functionName, min, max, points, user);

            logger.info("Function created: id={}", entity.getId());
            return ResponseEntity.ok(convertToDto(entity));

        } catch (Exception e) {
            logger.error("Error creating function from function", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * ✅ ИСПРАВЛЕНО: Правильный вызов метода Service
     * Получить все функции пользователя
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
     * ✅ ИСПРАВЛЕНО: Правильный вызов метода Service
     * Получить функцию по ID
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
     * ✅ ИСПРАВЛЕНО: Правильный вызов метода Service
     * Удалить функцию
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