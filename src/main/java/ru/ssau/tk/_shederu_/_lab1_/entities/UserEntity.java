package ru.ssau.tk._shederu_._lab1_.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(nullable = false, length = 255)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TabulatedFunctionEntity> tabulatedFunctions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompositeFunctionEntity> compositeFunctions = new ArrayList<>();

    public UserEntity() {}

    public UserEntity(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public UserEntity(Long id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public List<TabulatedFunctionEntity> getTabulatedFunctions() {
        return tabulatedFunctions;
    }

    public List<CompositeFunctionEntity> getCompositeFunctions() {
        return compositeFunctions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTabulatedFunctions(List<TabulatedFunctionEntity> tabulatedFunctions) {
        this.tabulatedFunctions = tabulatedFunctions;
    }

    public void setCompositeFunctions(List<CompositeFunctionEntity> compositeFunctions) {
        this.compositeFunctions = compositeFunctions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity)) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id)
                && Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }
}
