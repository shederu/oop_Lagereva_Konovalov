package ru.ssau.tk._shederu_._lab1_.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompositeFunctionDao extends JpaRepository<CompositeFunctionEntity, Long> {
    Optional<CompositeFunctionEntity> findByExpression(String expression);
    List<CompositeFunctionEntity> findByExpressionContaining(String substring);
    void deleteByExpression(String expression);
}
