package ru.ssau.tk._shederu_._lab1_.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
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
    private List<TabulatedFunctionEntity> tabulatedFunctions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompositeFunctionEntity> compositeFunctions;

    public UserEntity(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<TabulatedFunctionEntity> getTabulatedFunctions() {
        return tabulatedFunctions;
    }

    public void setTabulatedFunctions(List<TabulatedFunctionEntity> tabulatedFunctions) {
        this.tabulatedFunctions = tabulatedFunctions;
    }

    public List<CompositeFunctionEntity> getCompositeFunctions() {
        return compositeFunctions;
    }

    public void setCompositeFunctions(List<CompositeFunctionEntity> compositeFunctions) {
        this.compositeFunctions = compositeFunctions;
    }
}
