package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static ru.yandex.practicum.filmorate.util.Utils.getNextUserId;

@Service
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User addNewUser(User user) throws ValidationException {
        if (userValidation(user)) {
        }
        user.setId(getNextUserId());
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) throws ValidationException, NotFoundException {
        if (userValidation(user)) {
            if (!(users.keySet().contains(user.getId()))) {
                throw new NotFoundException("пользователь не известен");
            }
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> returnUsers() {
        return users.values();
    }

    @Override
    public User getUser(int id) throws NotFoundException {
        if (!(users.containsKey(id))) {
            throw new NotFoundException("пользователь не найден");
        }
        return users.get(id);
    }


    @Override
    public boolean userValidation(User user) throws ValidationException {
        var b = true;
        if ((user.getEmail().isBlank() || user.getEmail() == null) || !(user.getEmail().contains("@"))) {
            b = false;
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if ((user.getLogin().isBlank() || user.getLogin() == null) || user.getLogin().contains(" ")) {
            b = false;
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
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
