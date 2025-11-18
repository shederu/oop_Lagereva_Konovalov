package ru.ssau.tk._shederu_._lab1_.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TabulatedFunctionDao extends JpaRepository<TabulatedFunctionEntity, Long> {
    Optional<TabulatedFunctionEntity> findByName(String name);
    List<TabulatedFunctionEntity> findByNameContaining(String substring);
    boolean existsByName(String name);
    void deleteByName(String name);
}
