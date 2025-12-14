package ru.ssau.tk._shederu_._lab1_.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.Dao.CompositeFunctionDao;
import ru.ssau.tk._shederu_._lab1_.dto.CompositeFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.mapper.CompositeFunctionMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompositeFunctionService {
    private static final Logger logger = LoggerFactory.getLogger(CompositeFunctionService.class);
    private final CompositeFunctionDao compositeFunctionDao;

    public CompositeFunctionService(CompositeFunctionDao compositeFunctionDao) {
        this.compositeFunctionDao = compositeFunctionDao;
    }

    public Optional<CompositeFunctionDto> getFunctionById(Long id) {
        logger.info("Запрос композитной функции по id: {}", id);

        Optional<CompositeFunctionEntity> functionOpt = compositeFunctionDao.findById(id);

        if (functionOpt.isPresent()) {
            CompositeFunctionDto dto = CompositeFunctionMapper.toDTO(functionOpt.get());
            logger.info("Композитная функция id: {} трансформирована в DTO", id);
            return Optional.of(dto);
        }

        logger.debug("Композитная функция id: {} не найдена", id);
        return Optional.empty();
    }

    public Optional<CompositeFunctionDto> getFunctionByExpression(String expression) {
        logger.info("Запрос функции по выражению: {}", expression);

        Optional<CompositeFunctionEntity> functionOpt = compositeFunctionDao.findByExpression(expression);

        if (functionOpt.isPresent()) {
            CompositeFunctionDto dto = CompositeFunctionMapper.toDTO(functionOpt.get());
            logger.info("Функция с выражением {} трансформирована в DTO", expression);
            return Optional.of(dto);
        }

        logger.debug("Функция с выражением {} не найдена", expression);
        return Optional.empty();
    }

    public List<CompositeFunctionDto> getFunctionsByPattern(String pattern) {
        logger.info("Запрос функций по паттерну: {}", pattern);

        List<CompositeFunctionEntity> functions = compositeFunctionDao.findByExpressionContaining(pattern);

        List<CompositeFunctionDto> dtos = functions.stream()
                .map(CompositeFunctionMapper::toDTO)
                .collect(Collectors.toList());

        logger.info("Найдено {} функций по паттерну {}", dtos.size(), pattern);
        return dtos;
    }

    public List<CompositeFunctionDto> getFunctionsByUserId(Long userId) {
        logger.info("Запрос композитных функций пользователя id: {}", userId);

        List<CompositeFunctionEntity> functions = compositeFunctionDao.findByUserId(userId);

        List<CompositeFunctionDto> dtos = functions.stream()
                .map(CompositeFunctionMapper::toDTO)
                .collect(Collectors.toList());

        logger.info("Получено {} композитных функций пользователя {}", dtos.size(), userId);
        return dtos;
    }

    public List<CompositeFunctionDto> getAllFunctions() {
        logger.info("Запрос всех композитных функций");

        List<CompositeFunctionEntity> functions = compositeFunctionDao.findAll();

        List<CompositeFunctionDto> dtos = functions.stream()
                .map(CompositeFunctionMapper::toDTO)
                .collect(Collectors.toList());

        logger.info("Получено {} композитных функций", dtos.size());
        return dtos;
    }

    public CompositeFunctionDto createFunction(String expression, Long userId) {
        logger.info("Создание композитной функции: {} для пользователя: {}", expression, userId);

        CompositeFunctionEntity entity = new CompositeFunctionEntity(expression, userId);
        Long id = compositeFunctionDao.create(entity);

        if (id != null) {
            entity.setId(id);
            CompositeFunctionDto dto = CompositeFunctionMapper.toDTO(entity);
            logger.info("Композитная функция создана с id: {}", id);
            return dto;
        }

        logger.error("Не удалось создать композитную функцию");
        return null;
    }

    public boolean deleteFunction(Long id) {
        logger.info("Удаление композитной функции id: {}", id);
        return compositeFunctionDao.deleteById(id);
    }
}