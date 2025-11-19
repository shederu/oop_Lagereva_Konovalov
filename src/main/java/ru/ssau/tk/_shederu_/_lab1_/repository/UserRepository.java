package ru.ssau.tk._shederu_._lab1_.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByLogin(String login);

    boolean existsByLogin(String login);

    void deleteByLogin(String login);
}
