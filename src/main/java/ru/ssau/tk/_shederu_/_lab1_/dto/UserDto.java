package ru.ssau.tk._shederu_._lab1_.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.entities.RoleEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UserDto {
    private static final Logger logger = LoggerFactory.getLogger(UserDto.class);

    private Long id;
    private String login;
    private String password;
    private Set<RoleEntity> roles = new HashSet<>();

    public UserDto() {
        logger.trace("Создан пустой UserDto");
    }

    public UserDto(Long id, String login) {
        this.id = id;
        this.login = login;
        logger.trace("Создан UserDto: id={}, login={}", id, login);
    }

    public UserDto(Long id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
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

    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("roles")
    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(role -> role.getName().equals(roleName));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDto)) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id) &&
                Objects.equals(login, userDto.login) &&
                Objects.equals(password, userDto.password) &&
                Objects.equals(roles, userDto.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, roles);
    }

    @Override
    public String toString() {
        return "UserDto{id=" + id + ", login='" + login + "', roles=" + roles + "}";
    }
}