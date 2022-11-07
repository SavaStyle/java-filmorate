package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class User {

    private Integer id;
    private String email;
    @Pattern(regexp = "^\\S*$")
    private String login;
    private String name;
    private LocalDate birthday;

    public User(String email, String login, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

}
