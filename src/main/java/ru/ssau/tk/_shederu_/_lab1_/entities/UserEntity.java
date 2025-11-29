package ru.ssau.tk._shederu_._lab1_.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "`user`")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String login;

    @Column(nullable = false)
    private String password;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
