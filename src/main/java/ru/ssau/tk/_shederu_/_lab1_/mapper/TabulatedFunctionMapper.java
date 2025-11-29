package ru.ssau.tk._shederu_._lab1_.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;

public class TabulatedFunctionMapper {
    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionMapper.class);

    public static TabulatedFunctionDto toDTO(TabulatedFunctionEntity entity) {
        if (entity == null) {
            logger.warn("Попытка преобразования null TabulatedFunctionEntity в DTO");
            return null;
        }

        logger.debug("Преобразование TabulatedFunctionEntity (id={}, name={}, userId={}) в DTO",
                entity.getId(), entity.getName(), entity.getUserId());

        TabulatedFunctionDto dto = new TabulatedFunctionDto(
                entity.getId(),
                entity.getName(),
                entity.getData(),
                entity.getDerivative(),
                entity.getUserId()
        );

        logger.trace("TabulatedFunctionDto создан: id={}, name={}, dataLength={}, derivativeLength={}",
                dto.getId(), dto.getName(),
                dto.getData() != null ? dto.getData().length : 0,
                dto.getDerivative() != null ? dto.getDerivative().length : 0);

        return dto;
    }

    public static TabulatedFunctionEntity toEntity(TabulatedFunctionDto dto) {
        if (dto == null) {
            logger.warn("Попытка преобразования null TabulatedFunctionDto в Entity");
            return null;
        }

        logger.debug("Преобразование TabulatedFunctionDto (id={}, name={}, userId={}) в Entity",
                dto.getId(), dto.getName(), dto.getUserId());

        TabulatedFunctionEntity entity = new TabulatedFunctionEntity(
                dto.getName(),
                dto.getData(),
                dto.getDerivative(),
                dto.getUserId()
        );
        entity.setId(dto.getId());

        logger.trace("TabulatedFunctionEntity создан: id={}, name={}, dataLength={}, derivativeLength={}",
                entity.getId(), entity.getName(),
                entity.getData() != null ? entity.getData().length : 0,
                entity.getDerivative() != null ? entity.getDerivative().length : 0);

        return entity;
    }
}
