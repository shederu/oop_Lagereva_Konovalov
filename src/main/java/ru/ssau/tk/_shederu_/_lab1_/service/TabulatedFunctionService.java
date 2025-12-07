package ru.ssau.tk._shederu_._lab1_.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.repository.TabulatedFunctionRepository;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TabulatedFunctionService {

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    private TabulatedFunctionEntity dtoToEntity(TabulatedFunctionDto dto) {
        byte[] data = Base64.getDecoder().decode(dto.getData());
        byte[] derivative = Base64.getDecoder().decode(dto.getDerivative());
        return new TabulatedFunctionEntity(dto.getName(), data, derivative, dto.getUserId());
    }

    private TabulatedFunctionDto entityToDto(TabulatedFunctionEntity entity) {
        return new TabulatedFunctionDto(
                entity.getId(),
                entity.getName(),
                Base64.getEncoder().encodeToString(entity.getData()),
                Base64.getEncoder().encodeToString(entity.getDerivative()),
                entity.getUserId()
        );
    }

    public List<TabulatedFunctionDto> getAllFunctions() {
        return tabulatedFunctionRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public TabulatedFunctionDto getFunctionById(Long id) {
        return tabulatedFunctionRepository.findById(id)
                .map(this::entityToDto)
                .orElse(null);
    }

    public List<TabulatedFunctionDto> getFunctionsByUserId(Long userId) {
        return tabulatedFunctionRepository.findByUserId(userId)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public TabulatedFunctionDto createFunction(TabulatedFunctionDto functionDto) {
        TabulatedFunctionEntity entity = dtoToEntity(functionDto);
        TabulatedFunctionEntity saved = tabulatedFunctionRepository.save(entity);
        return entityToDto(saved);
    }

    public TabulatedFunctionDto updateFunction(Long id, TabulatedFunctionDto functionDto) {
        return tabulatedFunctionRepository.findById(id)
                .map(existing -> {
                    byte[] data = Base64.getDecoder().decode(functionDto.getData());
                    byte[] derivative = Base64.getDecoder().decode(functionDto.getDerivative());
                    existing.setName(functionDto.getName());
                    existing.setData(data);
                    existing.setDerivative(derivative);
                    existing.setUserId(functionDto.getUserId());
                    TabulatedFunctionEntity updated = tabulatedFunctionRepository.save(existing);
                    return entityToDto(updated);
                })
                .orElse(null);
    }

    public void deleteFunction(Long id) {
        tabulatedFunctionRepository.deleteById(id);
    }

    public List<TabulatedFunctionDto> findByName(String name) {
        return tabulatedFunctionRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
}
