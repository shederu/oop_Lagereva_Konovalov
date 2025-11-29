package ru.ssau.tk._shederu_._lab1_.mapper;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionMapperTest {

    private byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        return bytes;
    }

    @Test
    void testEntityToDTO() {
        byte[] data = generateRandomBytes(50);
        byte[] derivative = generateRandomBytes(50);

        TabulatedFunctionEntity entity = new TabulatedFunctionEntity(1L, "sin(x)", data, derivative, 5L);

        TabulatedFunctionDto dto = TabulatedFunctionMapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("sin(x)", dto.getName());
        assertArrayEquals(data, dto.getData());
        assertArrayEquals(derivative, dto.getDerivative());
        assertEquals(5L, dto.getUserId());
    }

    @Test
    void testDTOToEntity() {
        byte[] data = generateRandomBytes(50);
        byte[] derivative = generateRandomBytes(50);

        TabulatedFunctionDto dto = new TabulatedFunctionDto(
                1L, "cos(x)", data, derivative, 5L
        );

        TabulatedFunctionEntity entity = TabulatedFunctionMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("cos(x)", entity.getName());
        assertArrayEquals(data, entity.getData());
        assertArrayEquals(derivative, entity.getDerivative());
        assertEquals(5L, entity.getUserId());
    }

    @Test
    void testNullEntityToDTO() {
        TabulatedFunctionDto dto = TabulatedFunctionMapper.toDTO(null);
        assertNull(dto);
    }

    @Test
    void testNullDTOToEntity() {
        TabulatedFunctionEntity entity = TabulatedFunctionMapper.toEntity(null);
        assertNull(entity);
    }
}
