package ru.ssau.tk._shederu_._lab1_.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Base64;
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

    @JsonIgnore
    public byte[] getData() {
        return data;
    }

    @JsonIgnore
    public void setData(byte[] data) {
        logger.trace("TabulatedFunctionDto setData: length {} -> {}",
                this.data != null ? this.data.length : 0,
                data != null ? data.length : 0);
        this.data = data;
    }

    @JsonIgnore
    public byte[] getDerivative() {
        return derivative;
    }

    @JsonIgnore
    public void setDerivative(byte[] derivative) {
        logger.trace("TabulatedFunctionDto setDerivative: length {} -> {}",
                this.derivative != null ? this.derivative.length : 0,
                derivative != null ? derivative.length : 0);
        this.derivative = derivative;
    }

    @JsonProperty("data")
    public String getDataBase64() {
        if (data == null) return null;
        try {
            return Base64.getEncoder().encodeToString(data);
        } catch (Exception e) {
            logger.error("Error encoding data to base64", e);
            return null;
        }
    }

    @JsonProperty("data")
    public void setDataBase64(String dataBase64) {
        if (dataBase64 == null || dataBase64.isEmpty()) {
            this.data = null;
            logger.trace("TabulatedFunctionDto setDataBase64: null or empty");
        } else {
            try {
                this.data = Base64.getDecoder().decode(dataBase64);
                logger.trace("TabulatedFunctionDto setDataBase64: decoded {} bytes",
                        this.data != null ? this.data.length : 0);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid base64 data: {}", dataBase64, e);
                this.data = null;
            }
        }
    }

    @JsonProperty("derivative")
    public String getDerivativeBase64() {
        if (derivative == null) return null;
        try {
            return Base64.getEncoder().encodeToString(derivative);
        } catch (Exception e) {
            logger.error("Error encoding derivative to base64", e);
            return null;
        }
    }

    @JsonProperty("derivative")
    public void setDerivativeBase64(String derivativeBase64) {
        if (derivativeBase64 == null || derivativeBase64.isEmpty()) {
            this.derivative = null;
            logger.trace("TabulatedFunctionDto setDerivativeBase64: null or empty");
        } else {
            try {
                this.derivative = Base64.getDecoder().decode(derivativeBase64);
                logger.trace("TabulatedFunctionDto setDerivativeBase64: decoded {} bytes",
                        this.derivative != null ? this.derivative.length : 0);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid base64 derivative: {}", derivativeBase64, e);
                this.derivative = null;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        logger.trace("TabulatedFunctionDto setId: {} -> {}", this.id, id);
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        logger.trace("TabulatedFunctionDto setName: {} -> {}", this.name, name);
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        logger.trace("TabulatedFunctionDto setUserId: {} -> {}", this.userId, userId);
        this.userId = userId;
    }

    @JsonIgnore
    public boolean hasValidData() {
        return data != null && data.length > 0;
    }

    @JsonIgnore
    public boolean hasValidDerivative() {
        return derivative != null && derivative.length > 0;
    }

    @JsonIgnore
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
                userId != null &&
                hasValidData() &&
                hasValidDerivative();
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
        return "TabulatedFunctionDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", dataLength=" + (data != null ? data.length : 0) +
                ", derivativeLength=" + (derivative != null ? derivative.length : 0) +
                '}';
    }
}