package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void addNewFilmWithOutName() throws ValidationException {
        Film uno = new Film( "", "Описание", LocalDate.of(1990,1,10), 120);
        fc.addNewFilm(uno);
    }

    @Test
    void addNewFilmWrongRelisDate() throws ValidationException {
        Film uno = new Film( "uno", "Описание", LocalDate.of(1790,1,10), 120);
        fc.addNewFilm(uno);
    }

    @Test
    void addNewFilmNegativeDuration() throws ValidationException {
        Film uno = new Film( "uno", "Описание", LocalDate.of(1990,1,10), -120);
        fc.addNewFilm(uno);
    }

    @Test
    void addNewFilm200charsDiscription() throws ValidationException {
        Film uno = new Film("uno", "ие символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символов", LocalDate.of(1990,1,10), 120);
      fc.addNewFilm(uno);
    }

    @Test
    void addNewFilm201charsDiscription() throws ValidationException {
        Film uno = new Film("uno", "1ие символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символовОписание ольше 200 символов", LocalDate.of(1990,1,10), 120);
        fc.addNewFilm(uno);
    }

    @Test
    void updateFilm() throws ValidationException {
        Film uno = new Film("uno", "Описание", LocalDate.of(1895,12,28), 120);
        fc.addNewFilm(uno);
        var id = uno.getId();
        Film uno2 = new Film( "uno2", "Описание", LocalDate.of(1895,12,28), 120);
        fc.updateFilm(uno2);
        int size = fc.returnFilms().size();
        assertEquals(1, size);
    }
}