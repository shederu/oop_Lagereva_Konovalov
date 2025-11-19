package ru.ssau.tk._shederu_._lab1_.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.Dao.TabulatedFunctionDao;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.mapper.TabulatedFunctionMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TabulatedFunctionService {
    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionService.class);
    private final TabulatedFunctionDao tabulatedFunctionDao;

    public TabulatedFunctionService(TabulatedFunctionDao tabulatedFunctionDao) {
        this.tabulatedFunctionDao = tabulatedFunctionDao;
    }

    public Optional<TabulatedFunctionDto> getFunctionById(Long id) {
        logger.info("Запрос функции по id: {}", id);

        Optional<TabulatedFunctionEntity> functionOpt = tabulatedFunctionDao.findById(id);

        if (functionOpt.isPresent()) {
            TabulatedFunctionDto dto = TabulatedFunctionMapper.toDTO(functionOpt.get());
            logger.info("Функция id: {} трансформирована в DTO", id);
            return Optional.of(dto);
        }

        logger.debug("Функция id: {} не найдена", id);
        return Optional.empty();
    }

    public Optional<TabulatedFunctionDto> getFunctionByName(String name) {
        logger.info("Запрос функции по имени: {}", name);

        Optional<TabulatedFunctionEntity> functionOpt = tabulatedFunctionDao.findByName(name);

        if (functionOpt.isPresent()) {
            TabulatedFunctionDto dto = TabulatedFunctionMapper.toDTO(functionOpt.get());
            logger.info("Функция {} трансформирована в DTO", name);
            return Optional.of(dto);
        }

        logger.debug("Функция {} не найдена", name);
        return Optional.empty();
    }

    public List<TabulatedFunctionDto> getFunctionsByUserId(Long userId) {
        logger.info("Запрос функций пользователя id: {}", userId);

        List<TabulatedFunctionEntity> functions = tabulatedFunctionDao.findByUserId(userId);

        List<TabulatedFunctionDto> dtos = functions.stream()
                .map(TabulatedFunctionMapper::toDTO)
                .collect(Collectors.toList());

        logger.info("Получено {} функций пользователя {}, трансформировано в DTO", dtos.size(), userId);
        return dtos;
    }

    public List<TabulatedFunctionDto> getAllFunctions() {
        logger.info("Запрос всех функций");

        List<TabulatedFunctionEntity> functions = tabulatedFunctionDao.findAll();

        List<TabulatedFunctionDto> dtos = functions.stream()
                .map(TabulatedFunctionMapper::toDTO)
                .collect(Collectors.toList());

        logger.info("Получено {} функций, трансформировано в DTO", dtos.size());
        return dtos;
    }

    public TabulatedFunctionDto createFunction(String name, byte[] data, byte[] derivative, Long userId) {
        logger.info("Создание новой функции: {} для пользователя: {}", name, userId);

        TabulatedFunctionEntity entity = new TabulatedFunctionEntity(name, data, derivative, userId);
        Long id = tabulatedFunctionDao.create(entity);

        if (id != null) {
            entity.setId(id);
            TabulatedFunctionDto dto = TabulatedFunctionMapper.toDTO(entity);
            logger.info("Функция {} создана с id: {}", name, id);
            return dto;
        }

        logger.error("Не удалось создать функцию: {}", name);
        return null;
    }


    public boolean deleteFunction(Long id) {
        logger.info("Удаление функции id: {}", id);
        return tabulatedFunctionDao.deleteById(id);
    }
}
