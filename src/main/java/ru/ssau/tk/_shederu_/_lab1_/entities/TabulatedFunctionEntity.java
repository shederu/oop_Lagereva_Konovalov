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

    @Lob
    @Column(nullable = false)
    private byte[] data;

    @Lob
    @Column(nullable = false)
    private byte[] derivative;

    public TabulatedFunctionEntity(String name, byte[] data, byte[] derivative) {
        this.name = name;
        this.data = data;
        this.derivative = derivative;
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

}