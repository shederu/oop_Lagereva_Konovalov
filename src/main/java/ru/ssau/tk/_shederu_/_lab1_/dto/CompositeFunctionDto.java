package ru.ssau.tk._shederu_._lab1_.dto;

public class CompositeFunctionDto {
    private Long id;
    private String expression;

    public CompositeFunctionDto() {}

    public CompositeFunctionDto(Long id, String expression) {
        this.id = id;
        this.expression = expression;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getExpression() {

        return expression;
    }

    public void setExpression(String expression) {

        this.expression = expression;
    }
}
