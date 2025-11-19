package ru.ssau.tk._shederu_._lab1_.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.dto.UserDto;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

public class UserMapper {
    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    public static UserDto toDTO(UserEntity entity) {
        if (entity == null) {
            logger.warn("Попытка преобразования null UserEntity в DTO");
            return null;
        }

        logger.debug("Преобразование UserEntity (id={}, login={}) в UserDto",
                entity.getId(), entity.getLogin());

        UserDto dto = new UserDto(entity.getId(), entity.getLogin());

        logger.trace("UserDto создан: {}", dto);
        return dto;
    }

    public static UserEntity toEntity(UserDto dto, String password) {
        if (dto == null) {
            logger.warn("Попытка преобразования null UserDto в Entity");
            return null;
        }

        logger.debug("Преобразование UserDto (id={}, login={}) в UserEntity",
                dto.getId(), dto.getLogin());

        UserEntity entity = new UserEntity(dto.getLogin(), password);
        entity.setId(dto.getId());

        logger.trace("UserEntity создан: {}", entity);
        return entity;
    }
}
