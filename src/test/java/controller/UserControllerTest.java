package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class UserControllerTest {

    UserController uc;

    UserService userService;

   @BeforeEach
    void start() {
        uc = new UserController(userService);
    }

    @Test
    void addNewUserCorrect() throws ValidationException {
        User user1 = new User("user1@yandex.ru", "login",  LocalDate.of(1990,12,28));
        uc.addNewUser(user1);
    }

    @Test
    void addNewSpaseLogin() throws ValidationException {
        User user1 = new User("user1@yandex.ru", "log in",  LocalDate.of(1950,12,28));
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> uc.addNewUser(user1));
        assertEquals("логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void addNewBlankEmail() throws ValidationException {
        User user1 = new User(" ", "login",  LocalDate.of(1950,12,28));
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> uc.addNewUser(user1));
        assertEquals("электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    void addNewWithOutATEmail() throws ValidationException {
        User user1 = new User("user1yandex.ru", "login",  LocalDate.of(1950,12,28));
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> uc.addNewUser(user1));
        assertEquals("электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    void futureBirthday() throws ValidationException {
        User user1 = new User("user1@yandex.ru", "login",  LocalDate.of(3500,12,28));
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> uc.addNewUser(user1));
        assertEquals("дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void WithOutName() throws ValidationException {
        User user1 = new User("user1@yandex.ru", "login",  LocalDate.of(1990,12,28));
        uc.addNewUser(user1);
        System.out.println(uc.returnUsers());
    }


}