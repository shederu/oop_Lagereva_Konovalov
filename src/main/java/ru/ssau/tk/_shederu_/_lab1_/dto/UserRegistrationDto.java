package ru.ssau.tk._shederu_._lab1_.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRegistrationDto {
    private String login;
    private String password;

    public UserRegistrationDto() {}

    public UserRegistrationDto(@JsonProperty("login") String login, @JsonProperty("password") String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}