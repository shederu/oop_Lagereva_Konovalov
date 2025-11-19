package ru.ssau.tk._shederu_._lab1_.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class UserDto {
    private static final Logger logger = LoggerFactory.getLogger(UserDto.class);

    private Long id;
    private String login;

    public UserDto() {
        logger.trace("Создан пустой UserDto");
    }

    public UserDto(Long id, String login) {
        this.id = id;
        this.login = login;
        logger.trace("Создан UserDto: id={}, login={}", id, login);
    }

    public Long getId() { return id; }
    public void setId(Long id) {
        logger.trace("UserDto setId: {} -> {}", this.id, id);
        this.id = id;
    }

    public String getLogin() { return login; }
    public void setLogin(String login) {
        logger.trace("UserDto setLogin: {} -> {}", this.login, login);
        this.login = login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDto)) return false;
        UserDto userDTO = (UserDto) o;
        return Objects.equals(id, userDTO.id) && Objects.equals(login, userDTO.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login);
    }

    @Override
    public String toString() {
        return "UserDto{id=" + id + ", login='" + login + "'}";
    }
}
