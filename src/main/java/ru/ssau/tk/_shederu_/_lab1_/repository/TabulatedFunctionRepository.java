package ru.ssau.tk._shederu_._lab1_.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TabulatedFunctionRepository extends JpaRepository<TabulatedFunctionEntity, Long> {
    Optional<TabulatedFunctionEntity> findByName(String name);

    List<TabulatedFunctionEntity> findByNameContaining(String substring);

    List<TabulatedFunctionEntity> findByUserId(Long userId);

    Optional<TabulatedFunctionEntity> findByNameAndUserId(String name, Long userId);

    boolean existsByName(String name);

    boolean existsByNameAndUserId(String name, Long userId);

    void deleteByName(String name);

    void deleteByUserId(Long userId);

    long countByUserId(Long userId);

    List<TabulatedFunctionEntity> findByNameContainingIgnoreCase(String name);
}
