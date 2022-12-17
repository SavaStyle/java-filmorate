package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Data
public class UserService {

    private final UserStorage userStorage;

    public User addNewUser(User user) {
        try {
            userValidation(user);
            userStorage.addNewUser(user);
            return user;
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public User updateUser(User user) {
        userValidation(user);
        userStorage.isPresent(user.getId());
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public Optional<User> getUserById(int id) {
        if (!(userStorage.isPresent(id))) {
            throw new NotFoundException("Пользователь не найден");
        }
        return userStorage.getUserById(id);
    }

    public void addFriend(int userID, int friendId) {
        if (!(userStorage.isPresent(userID)) || !(userStorage.isPresent(friendId))) {
            throw new NotFoundException("Пользователь не найден");
        }
        userStorage.addFriend(userID, friendId);
    }

    public void deleteFriend(int userID, int friendId) {
        getUserById(userID);
        getUserById(friendId);
        userStorage.removeFriend(userID, friendId);
    }

    public List<User> getFriendsList(int userID) {
        getUserById(userID);
        return userStorage.getFriendsById(userID);
    }

    public List<User> getCommonFriends(int userID, int friendId) {
        getUserById(userID);
        getUserById(friendId);
        return userStorage.getCommonFriends(userID, friendId);
    }

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
