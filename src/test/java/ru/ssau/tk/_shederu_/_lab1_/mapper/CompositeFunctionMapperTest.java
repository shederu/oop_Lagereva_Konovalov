package ru.ssau.tk._shederu_._lab1_.mapper;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._shederu_._lab1_.dto.CompositeFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;

import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionMapperTest {

    @Test
    void testEntityToDTO() {
        CompositeFunctionEntity entity = new CompositeFunctionEntity(1L, "f(g(x))", 5L);

        CompositeFunctionDto dto = CompositeFunctionMapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("f(g(x))", dto.getExpression());
        assertEquals(5L, dto.getUserId());
    }

    @Test
    void testDTOToEntity() {
        CompositeFunctionDto dto = new CompositeFunctionDto(
                1L, "h(k(x))", 5L
        );

        CompositeFunctionEntity entity = CompositeFunctionMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("h(k(x))", entity.getExpression());
        assertEquals(5L, entity.getUserId());
    }

    @Test
    void testNullEntityToDTO() {
        CompositeFunctionDto dto = CompositeFunctionMapper.toDTO(null);
        assertNull(dto);
    }

    @Test
    void testNullDTOToEntity() {
        CompositeFunctionEntity entity = CompositeFunctionMapper.toEntity(null);
        assertNull(entity);
    }
}
