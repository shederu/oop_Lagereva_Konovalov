package ru.ssau.tk._shederu_._lab1_.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void testConstructor() {
        UserDto dto = new UserDto(1L, "john_doe");

        assertEquals(1L, dto.getId());
        assertEquals("john_doe", dto.getLogin());
    }

    @Test
    void testSettersGetters() {
        UserDto dto = new UserDto();
        dto.setId(2L);
        dto.setLogin("alice");

        assertEquals(2L, dto.getId());
        assertEquals("alice", dto.getLogin());
    }

    @Test
    void testEqualsAndHashCode() {
        UserDto dto1 = new UserDto(1L, "john_doe");
        UserDto dto2 = new UserDto(1L, "john_doe");
        UserDto dto3 = new UserDto(2L, "alice");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        UserDto dto = new UserDto(1L, "john_doe");
        String result = dto.toString();

        assertTrue(result.contains("john_doe"));
        assertTrue(result.contains("1"));
    }
}
