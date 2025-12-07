package ru.ssau.tk._shederu_._lab1_.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TabulatedFunctionDto {
    private Long id;
    private String name;
    @JsonProperty("data")
    private String data;
    @JsonProperty("derivative")
    private String derivative;
    private Long userId;

    public TabulatedFunctionDto() {}

    public TabulatedFunctionDto(String name, String data, String derivative, Long userId) {
        this.name = name;
        this.data = data;
        this.derivative = derivative;
        this.userId = userId;
    }

    public TabulatedFunctionDto(Long id, String name, String data, String derivative, Long userId) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.derivative = derivative;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getDerivative() { return derivative; }
    public void setDerivative(String derivative) { this.derivative = derivative; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
