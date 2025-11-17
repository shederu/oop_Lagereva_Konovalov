package ru.ssau.tk._shederu_._lab1_.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false, length = 150)
    private String login;

    @Column(nullable = false, length = 150)
    private String password;

    public UserEntity(String login, String password) {
        this.login = login;
        this.password = password;
    }
}