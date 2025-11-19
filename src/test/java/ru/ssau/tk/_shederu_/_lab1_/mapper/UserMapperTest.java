package ru.ssau.tk._shederu_._lab1_.mapper;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._shederu_._lab1_.dto.UserDto;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void testEntityToDTO() {
        UserEntity entity = new UserEntity(1L, "john_doe", "password123");

        UserDto dto = UserMapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("john_doe", dto.getLogin());
    }

    @Test
    void testDTOToEntity() {
        UserDto dto = new UserDto(1L, "john_doe");

        UserEntity entity = UserMapper.toEntity(dto, "newPassword");

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("john_doe", entity.getLogin());
        assertEquals("newPassword", entity.getPassword());
    }

    @Test
    void testNullEntityToDTO() {
        UserDto dto = UserMapper.toDTO(null);
        assertNull(dto);
    }

    @Test
    void testNullDTOToEntity() {
        UserEntity entity = UserMapper.toEntity(null, "password");
        assertNull(entity);
    }
}
