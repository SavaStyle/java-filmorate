package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Service
public interface UserStorage {
    User addNewUser(User user) throws ValidationException;

    User updateUser(User user) throws ValidationException, NotFoundException;

    Collection<User> returnUsers();

    User getUser(int id) throws NotFoundException;

    boolean userValidation(User user) throws ValidationException;
}
