package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/users")
    public User addNewUser(@Valid @RequestBody User user) throws ValidationException {
        return userService.getUserStorage().addNewUser(user);
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException, NotFoundException {
        return userService.getUserStorage().updateUser(user);
    }

    @GetMapping("/users")
    public Collection<User> returnUsers() {
        return userService.getUserStorage().returnUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable String id) throws NotFoundException {
        return userService.getUserStorage().getUser(Integer.parseInt(id));
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public HttpStatus addFriend(@PathVariable String id, @PathVariable String friendId) throws NotFoundException {
        userService.addFriend(Integer.parseInt(id), Integer.parseInt(friendId));
        return HttpStatus.OK;
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable String id, @PathVariable String friendId) throws NotFoundException {
        userService.deleteFriend(Integer.parseInt(id), Integer.parseInt(friendId));
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriendsList(@PathVariable String id) throws NotFoundException {
        return userService.getFriendsList(Integer.parseInt(id));
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable String id, @PathVariable String otherId) throws NotFoundException {
        return userService.getCommonFriends(Integer.parseInt(id), Integer.parseInt(otherId));
    }
}



