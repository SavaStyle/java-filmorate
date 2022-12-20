package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/users")
    public User addNewUser(@Valid @RequestBody User user) {
        return userService.addNewUser(user);
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/users")
    public Collection<User> returnUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public Optional<Optional<User>> getUserById(@PathVariable String id) {
        return Optional.ofNullable(userService.getUserById(Integer.parseInt(id)));
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public HttpStatus addFriend(@PathVariable String id, @PathVariable String friendId) {
        userService.addFriend(Integer.parseInt(id), Integer.parseInt(friendId));
        return HttpStatus.OK;
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable String id, @PathVariable String friendId) {
        userService.deleteFriend(Integer.parseInt(id), Integer.parseInt(friendId));
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriendsList(@PathVariable String id) {
        return userService.getFriendsList(Integer.parseInt(id));
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable String id, @PathVariable String otherId) {
        return userService.getCommonFriends(Integer.parseInt(id), Integer.parseInt(otherId));
    }

    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Integer id) {
        return userService.getRecommendations(id);
    }
}



