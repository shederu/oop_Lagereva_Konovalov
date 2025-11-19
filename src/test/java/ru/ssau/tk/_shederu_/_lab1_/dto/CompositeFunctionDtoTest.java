package ru.ssau.tk._shederu_._lab1_.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionDtoTest {

    @Test
    void testConstructor() {
        CompositeFunctionDto dto = new CompositeFunctionDto(1L, "f(g(x))", 5L);

        assertEquals(1L, dto.getId());
        assertEquals("f(g(x))", dto.getExpression());
        assertEquals(5L, dto.getUserId());
    }

    @Test
    void testSettersGetters() {
        CompositeFunctionDto dto = new CompositeFunctionDto();

        dto.setId(2L);
        dto.setExpression("h(k(x))");
        dto.setUserId(6L);

        assertEquals(2L, dto.getId());
        assertEquals("h(k(x))", dto.getExpression());
        assertEquals(6L, dto.getUserId());
    }

    @Test
    void testEqualsAndHashCode() {
        CompositeFunctionDto dto1 = new CompositeFunctionDto(1L, "f(g(x))", 5L);
        CompositeFunctionDto dto2 = new CompositeFunctionDto(1L, "f(g(x))", 5L);
        CompositeFunctionDto dto3 = new CompositeFunctionDto(2L, "h(k(x))", 6L);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        CompositeFunctionDto dto = new CompositeFunctionDto(1L, "f(g(x))", 5L);
        String result = dto.toString();

        assertTrue(result.contains("f(g(x))"));
        assertTrue(result.contains("5"));
    }
}
