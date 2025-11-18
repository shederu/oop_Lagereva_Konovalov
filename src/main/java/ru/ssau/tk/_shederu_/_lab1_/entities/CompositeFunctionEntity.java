package ru.ssau.tk._shederu_._lab1_.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "composite_function")
@Data
@NoArgsConstructor
public class CompositeFunctionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comp_func_seq")
    @SequenceGenerator(name = "comp_func_seq", sequenceName = "composite_function_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String expression;

    public CompositeFunctionEntity(String expression) {
        this.expression = expression;
    }

    public Long getId() {
        return id;
    }

    public String getExpression() {
        return expression;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

}