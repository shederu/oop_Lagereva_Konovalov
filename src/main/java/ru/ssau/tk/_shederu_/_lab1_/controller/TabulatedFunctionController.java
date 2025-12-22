package ru.ssau.tk._shederu_._lab1_.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.io.FunctionsIO;
import ru.ssau.tk._shederu_._lab1_.repository.UserRepository;
import ru.ssau.tk._shederu_._lab1_.service.TabulatedFunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tabulatedFunctions")
public class TabulatedFunctionController {

    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionController.class);

    @Autowired
    private TabulatedFunctionService functionService;

    @Autowired
    private UserRepository userRepository;

    /**
     * ✅ НОВЫЙ ЭНДПОИНТ: Создать функцию из массивов X и Y
     */
    @PostMapping("createFromArray")
    // @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')") // ❌ ВРЕМЕННО ОТКЛЮЧЕНО ДЛЯ ТЕСТА
    public ResponseEntity<Map<String, Object>> createFromArray(
            @RequestBody Map<String, List<Double>> request,
            Authentication auth) {
        try {
            System.out.println("🔴 DEBUG: auth = " + auth);
            System.out.println("🔴 DEBUG: auth.getName() = " + auth.getName());
            System.out.println("🔴 DEBUG: auth.getAuthorities() = " + auth.getAuthorities());
            System.out.println("🔴 DEBUG: auth.isAuthenticated() = " + auth.isAuthenticated());
            System.out.println("🔴 DEBUG: request = " + request);

            List<Double> xValues = request.get("x");
            List<Double> yValues = request.get("y");

            System.out.println("🔴 DEBUG: xValues = " + xValues);
            System.out.println("🔴 DEBUG: yValues = " + yValues);

            if (xValues == null || yValues == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "error", "message", "X и Y не должны быть null"));
            }

            if (xValues.size() != yValues.size()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "error", "message", "Размеры X и Y должны совпадать"));
            }

            UserEntity user = userRepository.findByLogin(auth.getName()).orElse(null);
            System.out.println("🔴 DEBUG: user = " + user);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "error", "message", "Пользователь не найден"));
            }

            String name = "Функция_" + System.currentTimeMillis();
            System.out.println("🔴 DEBUG: Calling createFromArray with name = " + name);

            TabulatedFunctionEntity saved = functionService.createFromArray(xValues, yValues, name, user);

            System.out.println("🔴 DEBUG: Function saved successfully with id = " + saved.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("id", saved.getId());
            response.put("name", saved.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            System.err.println("🔴 DEBUG: Validation error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", e.getMessage()));

        } catch (Exception e) {
            System.err.println("🔴 DEBUG: Exception caught: " + e.getClass().getName());
            System.err.println("🔴 DEBUG: Message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Ошибка: " + e.getMessage()));
        }
    }

    /**
     * ✅ НОВЫЙ ЭНДПОИНТ: Создать функцию из математической функции
     */
    @PostMapping("createFromFunction")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> createFromFunction(
            @RequestBody Map<String, Object> request,
            Authentication auth) {
        try {
            String functionName = (String) request.get("functionName");
            Double min = ((Number) request.get("min")).doubleValue();
            Double max = ((Number) request.get("max")).doubleValue();
            Integer points = ((Number) request.get("points")).intValue();

            UserEntity user = userRepository.findByLogin(auth.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "error", "message", "Пользователь не найден"));
            }

            TabulatedFunctionEntity saved = functionService.createFromFunction(
                    functionName, min, max, points, user);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("id", saved.getId());
            response.put("name", saved.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Error creating function from mathematical function", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Ошибка создания функции: " + e.getMessage()));
        }
    }

    @PostMapping("upload")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadFromFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "Uploaded Function") String name,
            Authentication auth) {
        try {
            TabulatedFunction function;
            try (BufferedInputStream is = new BufferedInputStream(file.getInputStream())) {
                function = FunctionsIO.deserialize(is);
            }

            UserEntity user = userRepository.findByLogin(auth.getName()).orElse(null);
            TabulatedFunctionEntity saved = functionService.saveFunctionToDb(function, name, user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("id", saved.getId());
            response.put("name", saved.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Upload error", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('CREATOR', 'VIEWER', 'ADMIN')")
    public ResponseEntity<byte[]> downloadFunction(@PathVariable Long id) {
        try {
            TabulatedFunction function = functionService.loadFunctionFromDb(id);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            FunctionsIO.serialize(new BufferedOutputStream(bos), function);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"function_" + id + ".bin\"")
                    .body(bos.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
