package ru.ssau.tk._shederu_._lab1_.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.repository.TabulatedFunctionRepository;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._shederu_._lab1_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._shederu_._lab1_.io.FunctionsIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TabulatedFunctionService {

    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionService.class);

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    private static final TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

    public TabulatedFunctionDto entityToDto(TabulatedFunctionEntity entity) {
        return new TabulatedFunctionDto(
                entity.getId(),
                entity.getName(),
                Base64.getEncoder().encodeToString(entity.getData()),
                Base64.getEncoder().encodeToString(entity.getDerivative()),
                entity.getUserId()
        );
    }

    private TabulatedFunctionEntity dtoToEntity(TabulatedFunctionDto dto) {
        byte[] data = Base64.getDecoder().decode(dto.getData());
        byte[] derivative = Base64.getDecoder().decode(dto.getDerivative());
        return new TabulatedFunctionEntity(dto.getName(), data, derivative, dto.getUserId());
    }

    public String serializeFunctionToBase64(TabulatedFunction function) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             BufferedOutputStream out = new BufferedOutputStream(bos)) {

            FunctionsIO.serialize(out, function);
            out.flush();

            byte[] serialized = bos.toByteArray();
            return Base64.getEncoder().encodeToString(serialized);

        } catch (IOException e) {
            logger.error("Error serializing function to Base64", e);
            throw new RuntimeException("Error serializing function", e);
        }
    }

    public TabulatedFunction deserializeFunctionFromBase64(String base64Data) {
        try {
            byte[] serialized = Base64.getDecoder().decode(base64Data);

            try (ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
                 BufferedInputStream in = new BufferedInputStream(bis)) {

                return FunctionsIO.deserialize(in);

            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error deserializing function from Base64", e);
            throw new RuntimeException("Error deserializing function", e);
        }
    }

    public TabulatedFunctionEntity saveFunctionToDb(TabulatedFunction function, String name, Long userId) {
        String base64Data = serializeFunctionToBase64(function);

        TabulatedFunctionEntity entity = new TabulatedFunctionEntity();
        entity.setName(name);
        entity.setData(Base64.getDecoder().decode(base64Data)); // Сохраняем raw байты в БД
        entity.setDerivative(new byte[0]); // Пока пустая производная
        entity.setUserId(userId);

        TabulatedFunctionEntity saved = tabulatedFunctionRepository.save(entity);
        logger.info("Function saved to DB: id={}, name={}", saved.getId(), saved.getName());

        return saved;
    }

    public TabulatedFunction loadFunctionFromDb(Long functionId) {
        TabulatedFunctionEntity entity = tabulatedFunctionRepository.findById(functionId)
                .orElseThrow(() -> {
                    logger.error("Function not found: {}", functionId);
                    return new IllegalArgumentException("Function not found: " + functionId);
                });

        String base64Data = Base64.getEncoder().encodeToString(entity.getData());
        return deserializeFunctionFromBase64(base64Data);
    }

    public TabulatedFunctionDto functionToDto(TabulatedFunction function, String name, Long userId) {
        String base64Data = serializeFunctionToBase64(function);

        return new TabulatedFunctionDto(
                null, // ID = null (т.к. еще не сохранена в БД)
                name != null ? name : "Result",
                base64Data,
                "", // Производная пока пустая
                userId != null ? userId : -1L
        );
    }
    public List<TabulatedFunctionDto> getFunctionsByUserId(Long userId) {
        List<TabulatedFunctionEntity> entities = tabulatedFunctionRepository.findByUserId(userId);
        logger.debug("Retrieved {} functions for user {}", entities.size(), userId);
        return entities.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public TabulatedFunctionDto getFunctionById(Long id) {
        logger.debug("Fetching function with ID: {}", id);
        return tabulatedFunctionRepository.findById(id)
                .map(entity -> {
                    logger.debug("Function found: {}", entity.getName());
                    return entityToDto(entity);
                })
                .orElse(null);
    }

    public TabulatedFunctionDto createFunction(TabulatedFunctionDto functionDto) {
        if (functionDto == null || functionDto.getData() == null || functionDto.getData().isEmpty()) {
            logger.warn("Invalid function DTO provided for creation");
            throw new IllegalArgumentException("Function data cannot be empty");
        }

        TabulatedFunctionEntity entity = dtoToEntity(functionDto);
        TabulatedFunctionEntity saved = tabulatedFunctionRepository.save(entity);

        logger.info("Function created: id={}, name={}", saved.getId(), saved.getName());
        return entityToDto(saved);
    }

    public TabulatedFunctionDto updateFunction(Long id, TabulatedFunctionDto functionDto) {
        if (functionDto == null || functionDto.getData() == null || functionDto.getData().isEmpty()) {
            logger.warn("Invalid function DTO provided for update");
            throw new IllegalArgumentException("Function data cannot be empty");
        }

        return tabulatedFunctionRepository.findById(id)
                .map(existing -> {
                    byte[] data = Base64.getDecoder().decode(functionDto.getData());
                    byte[] derivative = Base64.getDecoder().decode(
                            functionDto.getDerivative() != null && !functionDto.getDerivative().isEmpty()
                                    ? functionDto.getDerivative()
                                    : Base64.getEncoder().encodeToString(new byte[0])
                    );

                    existing.setName(functionDto.getName());
                    existing.setData(data);
                    existing.setDerivative(derivative);

                    TabulatedFunctionEntity updated = tabulatedFunctionRepository.save(existing);
                    logger.info("Function updated: id={}", id);

                    return entityToDto(updated);
                })
                .orElseThrow(() -> {
                    logger.error("Function not found for update: {}", id);
                    return new IllegalArgumentException("Function not found: " + id);
                });
    }

    public void deleteFunction(Long id) {
        if (!tabulatedFunctionRepository.existsById(id)) {
            logger.warn("Attempt to delete non-existent function: {}", id);
            throw new IllegalArgumentException("Function not found: " + id);
        }

        tabulatedFunctionRepository.deleteById(id);
        logger.info("Function deleted: id={}", id);
    }

    public List<TabulatedFunctionDto> findByName(String name) {
        logger.debug("Searching functions by name: {}", name);
        return tabulatedFunctionRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public List<TabulatedFunctionDto> getAllFunctions() {
        logger.debug("Fetching all functions");
        return tabulatedFunctionRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
}
