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
