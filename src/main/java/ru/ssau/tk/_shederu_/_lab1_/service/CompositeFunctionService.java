package ru.ssau.tk._shederu_._lab1_.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssau.tk._shederu_._lab1_.dto.CompositeFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.repository.CompositeFunctionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompositeFunctionService {

    @Autowired
    private CompositeFunctionRepository compositeFunctionRepository;

    private CompositeFunctionDto entityToDto(CompositeFunctionEntity entity) {
        return new CompositeFunctionDto(
                entity.getId(),
                entity.getExpression(),
                entity.getUserId()
        );
    }

    private CompositeFunctionEntity dtoToEntity(CompositeFunctionDto dto) {
        return new CompositeFunctionEntity(dto.getExpression(), dto.getUserId());
    }

    public List<CompositeFunctionDto> getAllFunctions() {
        return compositeFunctionRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public CompositeFunctionDto getFunctionById(Long id) {
        return compositeFunctionRepository.findById(id)
                .map(this::entityToDto)
                .orElse(null);
    }

    public List<CompositeFunctionDto> getFunctionsByUserId(Long userId) {
        return compositeFunctionRepository.findByUserId(userId)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public CompositeFunctionDto createFunction(CompositeFunctionDto functionDto) {
        CompositeFunctionEntity entity = dtoToEntity(functionDto);
        CompositeFunctionEntity saved = compositeFunctionRepository.save(entity);
        return entityToDto(saved);
    }

    public CompositeFunctionDto updateFunction(Long id, CompositeFunctionDto functionDto) {
        return compositeFunctionRepository.findById(id)
                .map(existing -> {
                    existing.setExpression(functionDto.getExpression());
                    existing.setUserId(functionDto.getUserId());
                    CompositeFunctionEntity updated = compositeFunctionRepository.save(existing);
                    return entityToDto(updated);
                })
                .orElse(null);
    }

    public void deleteFunction(Long id) {
        compositeFunctionRepository.deleteById(id);
    }

    public List<CompositeFunctionDto> findByExpression(String expression) {
        return compositeFunctionRepository.findByExpressionContainingIgnoreCase(expression)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
}
