package ru.ssau.tk._shederu_._lab1_.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._shederu_._lab1_.dto.CompositeFunctionDto;
import ru.ssau.tk._shederu_._lab1_.service.CompositeFunctionService;

import java.util.List;

@RestController
@RequestMapping("/api/composite-functions")
public class CompositeFunctionController {

    @Autowired
    private CompositeFunctionService compositeFunctionService;

    @GetMapping
    public ResponseEntity<List<CompositeFunctionDto>> getAllFunctions() {
        List<CompositeFunctionDto> functions = compositeFunctionService.getAllFunctions();
        return ResponseEntity.ok(functions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompositeFunctionDto> getFunctionById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        CompositeFunctionDto function = compositeFunctionService.getFunctionById(id);
        return function != null ? ResponseEntity.ok(function) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CompositeFunctionDto>> getFunctionsByUserId(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        List<CompositeFunctionDto> functions = compositeFunctionService.getFunctionsByUserId(userId);
        return ResponseEntity.ok(functions);
    }

    @PostMapping
    public ResponseEntity<CompositeFunctionDto> createFunction(@RequestBody CompositeFunctionDto functionDto) {
        if (functionDto == null || functionDto.getExpression() == null || functionDto.getExpression().trim().isEmpty() ||
                functionDto.getUserId() == null || functionDto.getUserId() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        CompositeFunctionDto created = compositeFunctionService.createFunction(functionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompositeFunctionDto> updateFunction(@PathVariable Long id, @RequestBody CompositeFunctionDto functionDto) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (functionDto == null || functionDto.getExpression() == null || functionDto.getExpression().trim().isEmpty() ||
                functionDto.getUserId() == null || functionDto.getUserId() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        CompositeFunctionDto updated = compositeFunctionService.updateFunction(id, functionDto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        CompositeFunctionDto existing = compositeFunctionService.getFunctionById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        compositeFunctionService.deleteFunction(id);
        return ResponseEntity.noContent().build();
    }
}
