package ru.ssau.tk._shederu_._lab1_.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.repository.TabulatedFunctionRepository;
import ru.ssau.tk._shederu_._lab1_.service.TabulatedFunctionService;

import java.util.List;

@RestController
@RequestMapping("/api/tabulated-functions")
public class TabulatedFunctionController {

    @Autowired
    private TabulatedFunctionService tabulatedFunctionService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TabulatedFunctionDto>> getFunctionsByUser(@PathVariable Long userId) {
        List<TabulatedFunctionDto> functions = tabulatedFunctionService.getFunctionsByUserId(userId);
        return ResponseEntity.ok(functions);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TabulatedFunctionDto>> searchFunctionsByName(@RequestParam String name) {
        List<TabulatedFunctionDto> functions = tabulatedFunctionService.findByName(name);
        return ResponseEntity.ok(functions);
    }

    @PostMapping
    public ResponseEntity<TabulatedFunctionDto> createFunction(@RequestBody TabulatedFunctionDto functionDto) {
        TabulatedFunctionDto createdFunction = tabulatedFunctionService.createFunction(functionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFunction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TabulatedFunctionDto> updateFunction(@PathVariable Long id, @RequestBody TabulatedFunctionDto functionDto) {
        try {
            TabulatedFunctionDto updatedFunction = tabulatedFunctionService.updateFunction(id, functionDto);
            return ResponseEntity.ok(updatedFunction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @GetMapping
    public List<TabulatedFunctionEntity> getAllFunctions() {
        return tabulatedFunctionRepository.findAll();
    }

    @GetMapping("/{id}")
    public TabulatedFunctionEntity getFunctionById(@PathVariable Long id) {
        return tabulatedFunctionRepository.findById(id).orElse(null);
    }

    @PostMapping
    public TabulatedFunctionEntity createFunction(@RequestBody TabulatedFunctionEntity function) {
        return tabulatedFunctionRepository.save(function);
    }

    @PutMapping("/{id}")
    public TabulatedFunctionEntity updateFunction(@PathVariable Long id, @RequestBody TabulatedFunctionEntity function) {
        function.setId(id);
        return tabulatedFunctionRepository.save(function);
    }

    @DeleteMapping("/{id}")
    public void deleteFunction(@PathVariable Long id) {
        tabulatedFunctionRepository.deleteById(id);
    }
}
