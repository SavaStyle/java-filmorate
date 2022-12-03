package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    FilmController fc;

    FilmService fs;

    FilmStorage filmStorage;
    UserStorage userStorage;

    @BeforeEach
    void start() {
        userStorage = new InMemoryUserStorage();
        filmStorage = new InMemoryFilmStorage();
        fs = new FilmService(filmStorage, userStorage);
        fc = new FilmController(fs);
    }

    @Test
    void addNewFilmCorrect() throws ValidationException {
        Film uno = new Film("uno", "Описание", LocalDate.of(1895, 12, 28), 100);
        fc.addNewFilm(uno);
    }

    @Test
    void addNewFilmWithOutName() {
        Film uno = new Film( "", "Описание", LocalDate.of(1990,1,10), 120);
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fc.addNewFilm(uno));
        assertEquals("Название не может бытьпустым", exception.getMessage());
    }

    @Test
    void addNewFilmWrongRelisDate()  {
        Film uno = new Film( "uno", "Описание", LocalDate.of(1790,1,10), 120);
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fc.addNewFilm(uno));
        assertEquals("Ошибочная дата релиза", exception.getMessage());
    }

    @Test
    void addNewFilmNegativeDuration() throws ValidationException {
        Film uno = new Film( "uno", "Описание", LocalDate.of(1990,1,10), -120);
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fc.addNewFilm(uno));
        assertEquals("продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    void addNewFilm200charsDiscription() throws ValidationException {
        Film uno = new Film("uno", "ие символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символов", LocalDate.of(1990,1,10), 120);
      fc.addNewFilm(uno);
    }

    @Test
    void addNewFilm201charsDiscription() throws ValidationException {
        Film uno = new Film("uno", "1ие символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символов", LocalDate.of(1990,1,10), 120);
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fc.addNewFilm(uno));
        assertEquals("максимальная длина описания — 200 символов", exception.getMessage());
    }

}