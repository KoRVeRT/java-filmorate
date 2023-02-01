package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User {
    private int id;

    @NotBlank
    private String login;

    @Email
    private String email;

    private String name;

    @PastOrPresent
    private LocalDate birthday;

    public User(int id, String login, String email, String name, LocalDate birthday) {
        this.id = id;
        this.login = login;
        this.email = email;
        if (name == null || name.isBlank()) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = birthday;
    }
}