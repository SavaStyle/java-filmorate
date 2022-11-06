package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;

@Data
@Slf4j
public class User {

    private Integer id;
    private String email; 
    private String login;
    private String name;
    private LocalDate birthday;

    public User(String email, String login, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

}
