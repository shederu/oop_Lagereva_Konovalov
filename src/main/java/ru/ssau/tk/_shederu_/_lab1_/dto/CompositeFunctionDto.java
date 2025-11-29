package ru.ssau.tk._shederu_._lab1_.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CompositeFunctionDto {
    private static final Logger logger = LoggerFactory.getLogger(CompositeFunctionDto.class);

    private Long id;
    private String expression;
    private Long userId;

    public CompositeFunctionDto() {
        logger.trace("Создан пустой CompositeFunctionDto");
    }

    public CompositeFunctionDto(Long id, String expression, Long userId) {
        this.id = id;
        this.expression = expression;
        this.userId = userId;

        logger.trace("Создан CompositeFunctionDto: id={}, expression={}, userId={}",
                id, expression, userId);
    }

    public Long getId() { return id; }
    public void setId(Long id) {
        logger.trace("CompositeFunctionDto setId: {} -> {}", this.id, id);
        this.id = id;
    }

    public String getExpression() { return expression; }
    public void setExpression(String expression) {
        logger.trace("CompositeFunctionDto setExpression: {} -> {}", this.expression, expression);
        this.expression = expression;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) {
        logger.trace("CompositeFunctionDto setUserId: {} -> {}", this.userId, userId);
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompositeFunctionDto)) return false;
        CompositeFunctionDto that = (CompositeFunctionDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(expression, that.expression) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, expression, userId);
    }

    @Override
    public String toString() {
        return "CompositeFunctionDto{" + "id=" + id + ", expression='" + expression + '\'' + ", userId=" + userId + '}';
    }
}
