package ru.ssau.tk._shederu_._lab1_.service;

import org.springframework.stereotype.Service;
import ru.ssau.tk._shederu_._lab1_.dto.CompositeFunctionDto;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class CompositeFunctionService {
    private final Map<Long, CompositeFunctionDto> functions = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<CompositeFunctionDto> getAllFunctions() {
        return new ArrayList<>(functions.values());
    }

    public CompositeFunctionDto getFunctionById(Long id) {
        return functions.get(id);
    }

    public List<CompositeFunctionDto> getFunctionsByUserId(Long userId) {
        return functions.values().stream()
                .filter(func -> func.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public CompositeFunctionDto createFunction(CompositeFunctionDto functionDto) {
        Long id = idCounter.getAndIncrement();
        functionDto.setId(id);
        functions.put(id, functionDto);
        return functionDto;
    }

    public CompositeFunctionDto updateFunction(Long id, CompositeFunctionDto functionDto) {
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
}