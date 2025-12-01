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
