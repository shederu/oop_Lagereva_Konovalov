package ru.ssau.tk._shederu_._lab1_.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompositeFunctionRepository extends JpaRepository<CompositeFunctionEntity, Long> {
    Optional<CompositeFunctionEntity> findByExpression(String expression);

    List<CompositeFunctionEntity> findByExpressionContaining(String pattern);

    List<CompositeFunctionEntity> findByUserId(Long userId);

    List<CompositeFunctionEntity> findByExpressionContainingAndUserId(String pattern, Long userId);

    boolean existsByExpression(String expression);

    void deleteByExpression(String expression);

    void deleteByUserId(Long userId);

    long countByUserId(Long userId);

    List<CompositeFunctionEntity> findByExpressionContainingIgnoreCase(String expression);
}
