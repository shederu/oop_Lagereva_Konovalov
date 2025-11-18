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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    public CompositeFunctionEntity(String expression, Long userId) {
        this.expression = expression;
        this.userId = userId;
    }

    // Lombok @Data генерирует getters/setters, но можно добавить явно для ясности
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
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
