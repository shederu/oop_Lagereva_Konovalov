package ru.ssau.tk._shederu_._lab1_.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

public class TabulatedFunctionDto {
    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionDto.class);

    private Long id;
    private String name;
    private byte[] data;
    private byte[] derivative;
    private Long userId;

    public TabulatedFunctionDto() {
        logger.trace("Создан пустой TabulatedFunctionDto");
    }

    public TabulatedFunctionDto(Long id, String name, byte[] data, byte[] derivative, Long userId) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.derivative = derivative;
        this.userId = userId;

        logger.trace("Создан TabulatedFunctionDto: id={}, name={}, userId={}, dataLength={}, derivativeLength={}",
                id, name, userId,
                data != null ? data.length : 0,
                derivative != null ? derivative.length : 0);
    }

    public Long getId() { return id; }
    public void setId(Long id) {
        logger.trace("TabulatedFunctionDto setId: {} -> {}", this.id, id);
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) {
        logger.trace("TabulatedFunctionDto setName: {} -> {}", this.name, name);
        this.name = name;
    }

    public byte[] getData() { return data; }
    public void setData(byte[] data) {
        logger.trace("TabulatedFunctionDto setData: length {} -> {}",
                this.data != null ? this.data.length : 0,
                data != null ? data.length : 0);
        this.data = data;
    }

    public byte[] getDerivative() { return derivative; }
    public void setDerivative(byte[] derivative) {
        logger.trace("TabulatedFunctionDto setDerivative: length {} -> {}",
                this.derivative != null ? this.derivative.length : 0,
                derivative != null ? derivative.length : 0);
        this.derivative = derivative;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) {
        logger.trace("TabulatedFunctionDto setUserId: {} -> {}", this.userId, userId);
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabulatedFunctionDto)) return false;
        TabulatedFunctionDto that = (TabulatedFunctionDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Arrays.equals(data, that.data) &&
                Arrays.equals(derivative, that.derivative) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, userId);
        result = 31 * result + Arrays.hashCode(data);
        result = 31 * result + Arrays.hashCode(derivative);
        return result;
    }

    @Override
    public String toString() {
        return "TabulatedFunctionDto{" + "id=" + id + ", name='" + name + '\'' + ", userId=" + userId + ", dataLength=" + (data != null ? data.length : 0) + ", derivativeLength=" + (derivative != null ? derivative.length : 0) + '}';
    }
}
