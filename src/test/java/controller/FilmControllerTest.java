package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    FilmController fc;

    @BeforeEach
    void start() {
        fc = new FilmController();
    }

    @Test
    void addNewFilmCorrect() throws ValidationException {
        Film uno = new Film( "uno", "Описание", LocalDate.of(1895,12,28), 100);
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