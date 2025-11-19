package ru.ssau.tk._shederu_._lab1_.dto;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionDtoTest {

    private byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        return bytes;
    }

    @Test
    void testConstructor() {
        byte[] data = generateRandomBytes(50);
        byte[] derivative = generateRandomBytes(50);

        TabulatedFunctionDto dto = new TabulatedFunctionDto(1L, "sin(x)", data, derivative, 5L);

        assertEquals(1L, dto.getId());
        assertEquals("sin(x)", dto.getName());
        assertArrayEquals(data, dto.getData());
        assertArrayEquals(derivative, dto.getDerivative());
        assertEquals(5L, dto.getUserId());
    }

    @Test
    void testSettersGetters() {
        byte[] data = generateRandomBytes(50);
        TabulatedFunctionDto dto = new TabulatedFunctionDto();

        dto.setId(1L);
        dto.setName("cos(x)");
        dto.setData(data);
        dto.setUserId(5L);

        assertEquals(1L, dto.getId());
        assertEquals("cos(x)", dto.getName());
        assertArrayEquals(data, dto.getData());
        assertEquals(5L, dto.getUserId());
    }

    @Test
    void testEqualsAndHashCode() {
        byte[] data = generateRandomBytes(50);

        TabulatedFunctionDto dto1 = new TabulatedFunctionDto(1L, "sin(x)", data, data, 5L);
        TabulatedFunctionDto dto2 = new TabulatedFunctionDto(1L, "sin(x)", data, data, 5L);
        TabulatedFunctionDto dto3 = new TabulatedFunctionDto(2L, "cos(x)", data, data, 6L);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        byte[] data = generateRandomBytes(50);
        TabulatedFunctionDto dto = new TabulatedFunctionDto(1L, "sin(x)", data, data, 5L);

        String result = dto.toString();

        assertTrue(result.contains("sin(x)"));
        assertTrue(result.contains("5"));
        assertTrue(result.contains("50"));
    }
}
