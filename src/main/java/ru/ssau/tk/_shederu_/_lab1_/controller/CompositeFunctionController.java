package ru.ssau.tk._shederu_._lab1_.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.repository.CompositeFunctionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/composite-functions")
public class CompositeFunctionController {

    @Autowired
    private CompositeFunctionRepository compositeFunctionRepository;

    @GetMapping
    public ResponseEntity<List<CompositeFunctionEntity>> getAllFunctions() {
        return ResponseEntity.ok(compositeFunctionRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompositeFunctionEntity> getFunctionById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return compositeFunctionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CompositeFunctionEntity> createFunction(@RequestBody CompositeFunctionEntity function) {
        if (function == null || function.getExpression() == null || function.getExpression().trim().isEmpty() ||
                function.getUserId() == null || function.getUserId() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        CompositeFunctionEntity saved = compositeFunctionRepository.save(function);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompositeFunctionEntity> updateFunction(@PathVariable Long id, @RequestBody CompositeFunctionEntity function) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        if (!compositeFunctionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (function == null || function.getExpression() == null || function.getExpression().trim().isEmpty() ||
                function.getUserId() == null || function.getUserId() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        function.setId(id);
        CompositeFunctionEntity updated = compositeFunctionRepository.save(function);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        if (!compositeFunctionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        compositeFunctionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
