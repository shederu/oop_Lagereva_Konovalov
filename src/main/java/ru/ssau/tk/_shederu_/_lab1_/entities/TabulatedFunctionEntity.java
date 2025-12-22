package ru.ssau.tk._shederu_._lab1_.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tabulated_function")
public class TabulatedFunctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "data", columnDefinition = "BYTEA", nullable = false)
    private byte[] data;

    @Column(name = "derivative", columnDefinition = "BYTEA", nullable = false)
    private byte[] derivative;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // ======================== CONSTRUCTORS ========================

    public TabulatedFunctionEntity() {
    }

    public TabulatedFunctionEntity(String name, byte[] data, byte[] derivative, UserEntity user) {
        this.name = name;
        this.data = data;
        this.derivative = derivative;
        this.user = user;
    }

    // ======================== GETTERS & SETTERS ========================

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

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    // ======================== TO STRING ========================

    @Override
    public String toString() {
        return "TabulatedFunctionEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", user=" + (user != null ? user.getLogin() : null) +
                '}';
    }
}
