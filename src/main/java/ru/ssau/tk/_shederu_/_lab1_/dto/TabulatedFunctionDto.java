package ru.ssau.tk._shederu_._lab1_.dto;

import java.util.List;

public class TabulatedFunctionDto {
    private Long id;
    private String name;
    private List<Double> xValues;
    private List<Double> yValues;
    private List<Double> derivativeYValues;

    public TabulatedFunctionDto() {}

    public TabulatedFunctionDto(Long id, String name, List<Double> xValues, List<Double> yValues) {
        this.id = id;
        this.name = name;
        this.xValues = xValues;
        this.yValues = yValues;
    }

    public TabulatedFunctionDto(Long id, String name, List<Double> xValues, List<Double> yValues, List<Double> derivativeYValues) {
        this.id = id;
        this.name = name;
        this.xValues = xValues;
        this.yValues = yValues;
        this.derivativeYValues = derivativeYValues;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getXValues() {
        return xValues;
    }

    public void setXValues(List<Double> xValues) {
        this.xValues = xValues;
    }

    public List<Double> getYValues() {
        return yValues;
    }

    public void setYValues(List<Double> yValues) {
        this.yValues = yValues;
    }

    public List<Double> getDerivativeYValues() {
        return derivativeYValues;
    }

    public void setDerivativeYValues(List<Double> derivativeYValues) {
        this.derivativeYValues = derivativeYValues;
    }
}
