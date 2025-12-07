package ru.ssau.tk._shederu_._lab1_.dto;

public class CompositeFunctionDto {
    private Long id;
    private String expression;
    private Long userId;

    public CompositeFunctionDto() {}

    public CompositeFunctionDto(String expression, Long userId) {
        this.expression = expression;
        this.userId = userId;
    }

    public CompositeFunctionDto(Long id, String expression, Long userId) {
        this.id = id;
        this.expression = expression;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
