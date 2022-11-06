package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


class UserControllerTest {

    UserController uc;

    @BeforeEach
    void start() {
        uc = new UserController();
    }

    @Test
    void addNewUserCorrect() throws ValidationException {
        User user1 = new User("user1@yandex.ru", "login",  LocalDate.of(1990,12,28));
        uc.addNewUser(user1);
    }

    @Test
    void addNewSpaseLogin() throws ValidationException {
        User user1 = new User("user1@yandex.ru", "log in",  LocalDate.of(1950,12,28));
        uc.addNewUser(user1);
    }

    @Test
    void addNewBlankEmail() throws ValidationException {
        User user1 = new User(" ", "login",  LocalDate.of(1950,12,28));
        uc.addNewUser(user1);
    }

    @Test
    void addNewWithOutATEmail() throws ValidationException {
        User user1 = new User("user1yandex.ru", "login",  LocalDate.of(1950,12,28));
        uc.addNewUser(user1);
    }

    @Test
    void futureBirthday() throws ValidationException {
        User user1 = new User("user1@yandex.ru", "login",  LocalDate.of(3500,12,28));
        uc.addNewUser(user1);
    }

    @Test
    void WithOutName() throws ValidationException {
        User user1 = new User("user1@yandex.ru", "login",  LocalDate.of(1990,12,28));
        uc.addNewUser(user1);
        System.out.println(uc.returnUsers());
    }


}