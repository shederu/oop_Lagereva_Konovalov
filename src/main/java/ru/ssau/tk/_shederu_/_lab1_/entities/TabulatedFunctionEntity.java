package ru.ssau.tk._shederu_._lab1_.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tabulated_function")
@Data
@NoArgsConstructor
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

    public TabulatedFunctionEntity(String name, byte[] data, byte[] derivative, Long userId) {
        this.name = name;
        this.data = data;
        this.derivative = derivative;
        this.userId = userId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
