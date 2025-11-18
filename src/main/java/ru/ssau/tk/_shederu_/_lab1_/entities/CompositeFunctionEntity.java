package ru.ssau.tk._shederu_._lab1_.entities;

public class CompositeFunctionEntity {
    private Long id;
    private String expression;
    private Long userId;

    public CompositeFunctionEntity() {}

    public CompositeFunctionEntity(String expression, Long userId) {
        this.expression = expression;
        this.userId = userId;
    }

    public CompositeFunctionEntity(Long id, String expression, Long userId) {
        this.id = id;
        this.expression = expression;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getExpression() {
        return expression;
    }

    public Long getUserId() {
        return userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
