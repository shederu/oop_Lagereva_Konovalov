package ru.ssau.tk._shederu_._lab1_.dto;

public class TabulatedFunctionDto {
    private Long id;
    private String name;
    private byte[] data;
    private byte[] derivative;

    public TabulatedFunctionDto() {}

    public TabulatedFunctionDto(Long id, String name, byte[] data, byte[] derivative) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.derivative = derivative;
    }

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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getDerivative() {
        return derivative;
    }

    public void setDerivative(byte[] derivative) {
        this.derivative = derivative;
    }
}
