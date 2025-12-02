package ru.ssau.tk._shederu_._lab1_.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.repository.TabulatedFunctionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/tabulated-functions")
public class TabulatedFunctionController {

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @GetMapping
    public ResponseEntity<List<TabulatedFunctionEntity>> getAllFunctions() {
        return ResponseEntity.ok(tabulatedFunctionRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TabulatedFunctionEntity> getFunctionById(@PathVariable(required = false) String id) {
        try {
            if (id == null || id.equals("null") || id.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Long longId = Long.parseLong(id);
            if (longId <= 0) {
                return ResponseEntity.badRequest().build();
            }

            return tabulatedFunctionRepository.findById(longId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping
    public ResponseEntity<TabulatedFunctionEntity> createFunction(@RequestBody TabulatedFunctionEntity function) {
        if (function == null || function.getName() == null || function.getName().trim().isEmpty() ||
                function.getData() == null || function.getDerivative() == null ||
                function.getUserId() == null || function.getUserId() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        TabulatedFunctionEntity saved = tabulatedFunctionRepository.save(function);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TabulatedFunctionEntity> updateFunction(@PathVariable Long id, @RequestBody TabulatedFunctionEntity function) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        if (!tabulatedFunctionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (function == null || function.getName() == null || function.getName().trim().isEmpty() ||
                function.getData() == null || function.getDerivative() == null ||
                function.getUserId() == null || function.getUserId() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        function.setId(id);
        TabulatedFunctionEntity updated = tabulatedFunctionRepository.save(function);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        if (!tabulatedFunctionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        tabulatedFunctionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
