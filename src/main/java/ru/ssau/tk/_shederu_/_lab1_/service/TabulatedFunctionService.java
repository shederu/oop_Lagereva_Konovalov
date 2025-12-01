package ru.ssau.tk._shederu_._lab1_.service;

import org.springframework.stereotype.Service;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TabulatedFunctionService {
    private final Map<Long, TabulatedFunctionDto> functions = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<TabulatedFunctionDto> getAllFunctions() {
        return new ArrayList<>(functions.values());
    }

    public TabulatedFunctionDto getFunctionById(Long id) {
        return functions.get(id);
    }

    public List<TabulatedFunctionDto> getFunctionsByUserId(Long userId) {
        return functions.values().stream()
                .filter(func -> func.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public TabulatedFunctionDto createFunction(TabulatedFunctionDto functionDto) {
        Long id = idCounter.getAndIncrement();
        functionDto.setId(id);
        functions.put(id, functionDto);
        return functionDto;
    }

    public TabulatedFunctionDto updateFunction(Long id, TabulatedFunctionDto functionDto) {
        if (!functions.containsKey(id)) {
            throw new RuntimeException("Function not found with id: " + id);
        }
        functionDto.setId(id);
        functions.put(id, functionDto);
        return functionDto;
    }

    public void deleteFunction(Long id) {
        if (!functions.containsKey(id)) {
            throw new RuntimeException("Function not found with id: " + id);
        }
        functions.remove(id);
    }

    public List<TabulatedFunctionDto> findByName(String name) {
        return functions.values().stream()
                .filter(func -> func.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
}