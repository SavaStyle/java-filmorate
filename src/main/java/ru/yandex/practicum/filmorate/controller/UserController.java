package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.util.Utils.getNextUserId;

@Slf4j
@RestController
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping(value = "/users")
    public User addNewUser(@Valid @RequestBody User user) throws ValidationException {
        if ((user.getEmail().isBlank() || user.getEmail() == null) || !(user.getEmail().contains("@"))) {
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if ((user.getLogin().isBlank() || user.getLogin() == null) || user.getLogin().contains(" ")) {
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null  || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if ((user.getEmail().isBlank() || user.getEmail() == null) || !(user.getEmail().contains("@"))) {
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if ((user.getLogin().isBlank() || user.getLogin() == null) || user.getLogin().contains(" ")) {
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null  || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        if (!(users.keySet().contains(user.getId()))) {
            throw new ValidationException("пользователь не известен");
        }
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping("/users")
    public  Collection<User>returnUsers() {
        Collection<User> userList = users.values();
        return userList;
    }

}
