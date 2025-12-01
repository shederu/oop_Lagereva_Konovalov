package ru.ssau.tk._shederu_._lab1_.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._shederu_._lab1_.dto.CompositeFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.repository.CompositeFunctionRepository;
import ru.ssau.tk._shederu_._lab1_.service.CompositeFunctionService;

import java.util.List;

@RestController
@RequestMapping("/api/composite-functions")
public class CompositeFunctionController {

    @Autowired
    private CompositeFunctionService compositeFunctionService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CompositeFunctionDto>> getFunctionsByUser(@PathVariable Long userId) {
        List<CompositeFunctionDto> functions = compositeFunctionService.getFunctionsByUserId(userId);
        return ResponseEntity.ok(functions);
    }

    @PostMapping
    public ResponseEntity<CompositeFunctionDto> createFunction(@RequestBody CompositeFunctionDto functionDto) {
        CompositeFunctionDto createdFunction = compositeFunctionService.createFunction(functionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFunction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompositeFunctionDto> updateFunction(@PathVariable Long id, @RequestBody CompositeFunctionDto functionDto) {
        try {
            CompositeFunctionDto updatedFunction = compositeFunctionService.updateFunction(id, functionDto);
            return ResponseEntity.ok(updatedFunction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Autowired
    private CompositeFunctionRepository compositeFunctionRepository;

    @GetMapping
    public List<CompositeFunctionEntity> getAllFunctions() {
        return compositeFunctionRepository.findAll();
    }

    @GetMapping("/{id}")
    public CompositeFunctionEntity getFunctionById(@PathVariable Long id) {
        return compositeFunctionRepository.findById(id).orElse(null);
    }

    @PostMapping
    public CompositeFunctionEntity createFunction(@RequestBody CompositeFunctionEntity function) {
        return compositeFunctionRepository.save(function);
    }

    @PutMapping("/{id}")
    public CompositeFunctionEntity updateFunction(@PathVariable Long id, @RequestBody CompositeFunctionEntity function) {
        function.setId(id);
        return compositeFunctionRepository.save(function);
    }

    @DeleteMapping("/{id}")
    public void deleteFunction(@PathVariable Long id) {
        compositeFunctionRepository.deleteById(id);
    }
}
