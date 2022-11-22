package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;

@Data
public class User {

    private Integer id;
    private String email;
    @Pattern(regexp = "^\\S*$")
    private String login;
    private String name;
    private LocalDate birthday;

    Set<Integer> friends;

    public User(String email, String login, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

}
