package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.util.Utils.getNextUserId;

@RestController
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping(value = "/users")
    public User addNewUser(@Valid @RequestBody User user) throws ValidationException {
        if (userValidation(user)) {
            user.setId(getNextUserId());
            users.put(user.getId(), user);
        }
        return user;
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (userValidation(user)) {
            if (!(users.keySet().contains(user.getId()))) {
                throw new ValidationException("пользователь не известен");
            }
            user.setId(getNextUserId());
            users.put(user.getId(), user);
        }
        return user;
    }

    @GetMapping("/users")
    public Collection<User>returnUsers() {
        Collection<User> userList = users.values();
        return userList;
    }

    private boolean userValidation(User user) throws ValidationException {
        var b = true;
        if ((user.getEmail().isBlank() || user.getEmail() == null) || !(user.getEmail().contains("@"))) {
            b = false;
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if ((user.getLogin().isBlank() || user.getLogin() == null) || user.getLogin().contains(" ")) {
            b = false;
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null  || user.getName().isBlank() || user.getName().isEmpty()) {
            b = false;
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            b = false;
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        return b;
    }
}
