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
}