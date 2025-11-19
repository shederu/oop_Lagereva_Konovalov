package ru.ssau.tk._shederu_._lab1_.entities;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "tabulated_function")
public class TabulatedFunctionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tab_func_seq")
    @SequenceGenerator(name = "tab_func_seq", sequenceName = "tabulated_function_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] data;

    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] derivative;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    public TabulatedFunctionEntity() {}

    public TabulatedFunctionEntity(String name, byte[] data, byte[] derivative, Long userId) {
        this.name = name;
        this.data = data;
        this.derivative = derivative;
        this.userId = userId;
    }

    public TabulatedFunctionEntity(Long id, String name, byte[] data, byte[] derivative, Long userId, UserEntity user) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.derivative = derivative;
        this.userId = userId;
        this.user = user;
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

    public UserEntity getUser() {
        return user;
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

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabulatedFunctionEntity)) return false;
        TabulatedFunctionEntity that = (TabulatedFunctionEntity) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Arrays.equals(data, that.data)
                && Arrays.equals(derivative, that.derivative)
                && Objects.equals(userId, that.userId);
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
        return "TabulatedFunctionEntity{" + "id=" + id + ", name='" + name + '\'' + ", userId=" + userId + ", dataLength=" + (data != null ? data.length : 0) + ", derivativeLength=" + (derivative != null ? derivative.length : 0) + '}';
    }
}
