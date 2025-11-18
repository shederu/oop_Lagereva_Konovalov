package ru.ssau.tk._shederu_._lab1_.entities;



public class TabulatedFunctionEntity {
    private Long id;
    private String name;
    private byte[] data;
    private byte[] derivative;
    private Long userId;

    public TabulatedFunctionEntity() {}

    public TabulatedFunctionEntity(String name, byte[] data, byte[] derivative, Long userId) {
        this.name = name;
        this.data = data;
        this.derivative = derivative;
        this.userId = userId;
    }

    public TabulatedFunctionEntity(Long id, String name, byte[] data, byte[] derivative, Long userId) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.derivative = derivative;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getDerivative() {
        return derivative;
    }

    public Long getUserId() {
        return userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setDerivative(byte[] derivative) {
        this.derivative = derivative;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
