package ru.ssau.tk._shederu_._lab1_.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._shederu_._lab1_.operations.TabulatedDifferentialOperator;
import ru.ssau.tk._shederu_._lab1_.operations.TabulatedFunctionOperationService;
import ru.ssau.tk._shederu_._lab1_.repository.UserRepository;
import ru.ssau.tk._shederu_._lab1_.service.TabulatedFunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/operations")
public class OperationsController {

    private static final Logger logger = LoggerFactory.getLogger(OperationsController.class);

    @Autowired
    private TabulatedFunctionService functionService;

    @Autowired
    private UserRepository userRepository;

    private final TabulatedFunctionOperationService operationService =
            new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());

    private final TabulatedDifferentialOperator differentialOperator =
            new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());

    // ================== БИНАРНЫЕ ОПЕРАЦИИ (без сохранения) ==================

    @PostMapping("/{operation}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> performOperation(
            @PathVariable String operation,
            @RequestParam Long id1,
            @RequestParam Long id2,
            Authentication auth) {

        try {
            logger.info("User {} performing operation: {} on functions {} and {}",
                    auth.getName(), operation, id1, id2);

            if (id1 == null || id1 <= 0 || id2 == null || id2 <= 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Invalid function IDs"));
            }

            TabulatedFunction f1 = functionService.loadFunctionFromDb(id1);
            TabulatedFunction f2 = functionService.loadFunctionFromDb(id2);

            TabulatedFunction result;
            switch (operation.toLowerCase()) {
                case "add":
                    result = operationService.add(f1, f2);
                    break;
                case "subtract":
                    result = operationService.subtract(f1, f2);
                    break;
                case "multiply":
                    result = operationService.multiply(f1, f2);
                    break;
                case "divide":
                    result = operationService.divide(f1, f2);
                    break;
                default:
                    return ResponseEntity.badRequest()
                            .body(createErrorResponse("Unknown operation"));
            }

            UserEntity user = userRepository.findByLogin(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            TabulatedFunctionDto resultDto =
                    functionService.functionToDto(result, "Result of " + operation, user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("operation", operation);
            response.put("result", resultDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // ================== БИНАРНЫЕ ОПЕРАЦИИ (с сохранением) ==================

    @PostMapping("/{operation}/save")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> performOperationAndSave(
            @PathVariable String operation,
            @RequestParam Long id1,
            @RequestParam Long id2,
            @RequestParam(defaultValue = "Result") String resultName,
            Authentication auth) {

        try {
            TabulatedFunction f1 = functionService.loadFunctionFromDb(id1);
            TabulatedFunction f2 = functionService.loadFunctionFromDb(id2);

            TabulatedFunction result;
            switch (operation.toLowerCase()) {
                case "add":
                    result = operationService.add(f1, f2);
                    break;
                case "subtract":
                    result = operationService.subtract(f1, f2);
                    break;
                case "multiply":
                    result = operationService.multiply(f1, f2);
                    break;
                case "divide":
                    result = operationService.divide(f1, f2);
                    break;
                default:
                    return ResponseEntity.badRequest()
                            .body(createErrorResponse("Unknown operation"));
            }

            UserEntity user = userRepository.findByLogin(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            TabulatedFunctionEntity savedEntity =
                    functionService.saveFunctionToDb(result, resultName, user);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("saved", true);
            response.put("id", savedEntity.getId());
            response.put("name", savedEntity.getName());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // ================== ПРОИЗВОДНЫЕ (без сохранения) ==================

    @PostMapping("/derive")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> deriveFunction(@RequestParam Long id, Authentication auth) {
        return performDerivation(id, "simple", auth);
    }

    @PostMapping("/derive-average")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> deriveWithAverage(@RequestParam Long id, Authentication auth) {
        return performDerivation(id, "average", auth);
    }

    @PostMapping("/derive-sync")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> deriveSynchronously(@RequestParam Long id, Authentication auth) {
        return performDerivation(id, "sync", auth);
    }

    private ResponseEntity<?> performDerivation(Long id, String method, Authentication auth) {
        try {
            TabulatedFunction f = functionService.loadFunctionFromDb(id);

            TabulatedFunction result;
            switch (method) {
                case "average":
                    result = differentialOperator.deriveWithAverage(f);
                    break;
                case "sync":
                    result = differentialOperator.deriveSynchronously(f);
                    break;
                default:
                    result = differentialOperator.derive(f);
                    break;
            }

            UserEntity user = userRepository.findByLogin(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            TabulatedFunctionDto resultDto =
                    functionService.functionToDto(result, "Derivative", user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("method", method);
            response.put("result", resultDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // ================== ПРОИЗВОДНЫЕ (с сохранением) ==================

    @PostMapping("/derive/save")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> deriveAndSave(
            @RequestParam Long id,
            @RequestParam(defaultValue = "Derivative") String resultName,
            @RequestParam(defaultValue = "simple") String method,
            Authentication auth) {

        try {
            TabulatedFunction f = functionService.loadFunctionFromDb(id);

            TabulatedFunction result;
            switch (method.toLowerCase()) {
                case "average":
                    result = differentialOperator.deriveWithAverage(f);
                    break;
                case "sync":
                    result = differentialOperator.deriveSynchronously(f);
                    break;
                default:
                    result = differentialOperator.derive(f);
                    break;
            }

            UserEntity user = userRepository.findByLogin(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            TabulatedFunctionEntity savedEntity =
                    functionService.saveFunctionToDb(result, resultName, user);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("saved", true);
            response.put("id", savedEntity.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // ================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==================

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return response;
    }
}
