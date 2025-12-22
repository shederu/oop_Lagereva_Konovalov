package ru.ssau.tk._shederu_._lab1_.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TabulatedFunctionRepository extends JpaRepository<TabulatedFunctionEntity, Long> {

    // ==================== СТАРЫЕ МЕТОДЫ ====================
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


    // ==================== НОВЫЕ МЕТОДЫ ====================

    /**
     * Найти все функции конкретного пользователя (по UserEntity)
     */
    List<TabulatedFunctionEntity> findByUser(UserEntity user);

    /**
     * Найти функцию по ID и пользователю (для проверки прав доступа)
     */
    Optional<TabulatedFunctionEntity> findByIdAndUser(Long id, UserEntity user);

    /**
     * Удалить функцию по ID и пользователю
     */
    void deleteByIdAndUser(Long id, UserEntity user);

    /**
     * Найти функцию по имени и пользователю (по UserEntity)
     */
    Optional<TabulatedFunctionEntity> findByNameAndUser(String name, UserEntity user);


}
