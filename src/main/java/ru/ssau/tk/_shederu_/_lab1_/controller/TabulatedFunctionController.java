package ru.ssau.tk._shederu_._lab1_.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.service.TabulatedFunctionService;

import java.util.List;

@RestController
@RequestMapping("/api/tabulated-functions")
public class TabulatedFunctionController {

    @Autowired
    private TabulatedFunctionService tabulatedFunctionService;

    @GetMapping
    public ResponseEntity<List<TabulatedFunctionDto>> getAllFunctions() {
        List<TabulatedFunctionDto> functions = tabulatedFunctionService.getAllFunctions();
        return ResponseEntity.ok(functions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TabulatedFunctionDto> getFunctionById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        TabulatedFunctionDto function = tabulatedFunctionService.getFunctionById(id);
        return function != null ? ResponseEntity.ok(function) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TabulatedFunctionDto>> getFunctionsByUserId(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        List<TabulatedFunctionDto> functions = tabulatedFunctionService.getFunctionsByUserId(userId);
        return ResponseEntity.ok(functions);
    }

    @PostMapping
    public ResponseEntity<TabulatedFunctionDto> createFunction(@RequestBody TabulatedFunctionDto functionDto) {
        if (functionDto == null || functionDto.getName() == null || functionDto.getName().trim().isEmpty() ||
                functionDto.getData() == null || functionDto.getDerivative() == null ||
                functionDto.getUserId() == null || functionDto.getUserId() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        TabulatedFunctionDto created = tabulatedFunctionService.createFunction(functionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TabulatedFunctionDto> updateFunction(@PathVariable Long id, @RequestBody TabulatedFunctionDto functionDto) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (functionDto == null || functionDto.getName() == null || functionDto.getName().trim().isEmpty() ||
                functionDto.getData() == null || functionDto.getDerivative() == null ||
                functionDto.getUserId() == null || functionDto.getUserId() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        TabulatedFunctionDto updated = tabulatedFunctionService.updateFunction(id, functionDto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        TabulatedFunctionDto existing = tabulatedFunctionService.getFunctionById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        tabulatedFunctionService.deleteFunction(id);
        return ResponseEntity.noContent().build();
    }
}
