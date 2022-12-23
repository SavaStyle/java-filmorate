package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.storage.impl.FeedDbStorage.*;

@Component
@RequiredArgsConstructor
@Data
public class UserService {

    private final UserStorage userStorage;
    private final FeedStorage feedStorage;
    private final FilmStorage filmStorage;

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
        feedStorage.addFeed(userID, FRIEND, ADD, friendId);
    }

    public void deleteFriend(int userID, int friendId) {
        getUserById(userID);
        getUserById(friendId);
        userStorage.removeFriend(userID, friendId);
        feedStorage.addFeed(userID, FRIEND, REMOVE, friendId);
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

    public void deleteUserById(int id) {
        userStorage.removeUserById(id);
    }

    public List<Film> getRecommendations(int userID) {
        return filmStorage.getRecommendations(userID);
    }

    public List<Feed> getFeed(int userId) {
        Optional<User> optionalUser = getUserById(userId);
        User user = optionalUser.orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return feedStorage.getFeed(userId);
    }
}
