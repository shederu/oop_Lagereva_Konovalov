package ru.ssau.tk._shederu_._lab1_.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.dto.CompositeFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;

public class CompositeFunctionMapper {
    private static final Logger logger = LoggerFactory.getLogger(CompositeFunctionMapper.class);

    public static CompositeFunctionDto toDTO(CompositeFunctionEntity entity) {
        if (entity == null) {
            logger.warn("Попытка преобразования null CompositeFunctionEntity в DTO");
            return null;
        }

        logger.debug("Преобразование CompositeFunctionEntity (id={}, expression={}, userId={}) в DTO",
                entity.getId(), entity.getExpression(), entity.getUserId());

        CompositeFunctionDto dto = new CompositeFunctionDto(
                entity.getId(),
                entity.getExpression(),
                entity.getUserId()
        );

        logger.trace("CompositeFunctionDto создан: {}", dto);
        return dto;
    }

    public static CompositeFunctionEntity toEntity(CompositeFunctionDto dto) {
        if (dto == null) {
            logger.warn("Попытка преобразования null CompositeFunctionDto в Entity");
            return null;
        }

        logger.debug("Преобразование CompositeFunctionDto (id={}, expression={}, userId={}) в Entity",
                dto.getId(), dto.getExpression(), dto.getUserId());

        CompositeFunctionEntity entity = new CompositeFunctionEntity(dto.getExpression(), dto.getUserId());
        entity.setId(dto.getId());

        logger.trace("CompositeFunctionEntity создан: {}", entity);
        return entity;
    }
}
