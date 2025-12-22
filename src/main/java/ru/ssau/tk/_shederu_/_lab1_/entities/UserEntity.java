package ru.ssau.tk._shederu_._lab1_.entities;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "users_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TabulatedFunctionEntity> tabulatedFunctions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompositeFunctionEntity> compositeFunctions = new ArrayList<>();

    // ======================== CONSTRUCTORS ========================

    public UserEntity() {}

    public UserEntity(String login, String password) {
        this.login = login;
        this.password = password;
    }

    // ======================== GETTERS & SETTERS ========================

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

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
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

    // ======================== UTILITY METHODS ========================

    /**
     * Добавить табулированную функцию пользователю
     */
    public void addTabulatedFunction(TabulatedFunctionEntity function) {
        if (function != null) {
            tabulatedFunctions.add(function);
            function.setUser(this);
        }
    }

    /**
     * Удалить табулированную функцию у пользователя
     */
    public void removeTabulatedFunction(TabulatedFunctionEntity function) {
        if (function != null) {
            tabulatedFunctions.remove(function);
            function.setUser(null);
        }
    }

    /**
     * Добавить композитную функцию пользователю
     */
    public void addCompositeFunction(CompositeFunctionEntity function) {
        if (function != null) {
            compositeFunctions.add(function);
            function.setUser(this);
        }
    }

    /**
     * Удалить композитную функцию у пользователя
     */
    public void removeCompositeFunction(CompositeFunctionEntity function) {
        if (function != null) {
            compositeFunctions.remove(function);
            function.setUser(null);
        }
    }

    /**
     * Получить количество всех функций пользователя
     */
    public int getTotalFunctionCount() {
        return tabulatedFunctions.size() + compositeFunctions.size();
    }

    /**
     * Проверить, существует ли функция у пользователя по имени
     */
    public boolean hasTabulatedFunction(String name) {
        return tabulatedFunctions.stream()
                .anyMatch(f -> f.getName() != null && f.getName().equals(name));
    }

    /**
     * Найти табулированную функцию по имени
     */
    public Optional<TabulatedFunctionEntity> findTabulatedFunction(String name) {
        return tabulatedFunctions.stream()
                .filter(f -> f.getName() != null && f.getName().equals(name))
                .findFirst();
    }

    /**
     * Найти композитную функцию по выражению
     */
    public Optional<CompositeFunctionEntity> findCompositeFunction(String expression) {
        return compositeFunctions.stream()
                .filter(f -> f.getExpression() != null && f.getExpression().equals(expression))
                .findFirst();
    }

    // ======================== EQUALS & HASHCODE ========================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return login != null ? login.equals(that.login) : that.login == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        return result;
    }

    // ======================== TO STRING ========================

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", roles=" + roles +
                ", tabulatedFunctions=" + tabulatedFunctions.size() +
                ", compositeFunctions=" + compositeFunctions.size() +
                '}';
    }
}
