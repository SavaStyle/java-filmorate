package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.util.Utils.getNextFilmId;

@RestController
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping("/films")
    public Film addNewFilm( @RequestBody Film film) throws ValidationException {
        if (filmValidation(film)) {
            film.setId(getNextFilmId());
            films.put(film.getId(), film);
        }
        return film;
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        if (filmValidation(film)) {
            if (!(films.keySet().contains(film.getId()))) {
                throw new ValidationException("фильм не известен");
            }
            film.setId(getNextFilmId());
            films.put(film.getId(), film);
        }
        return film;
    }

    @GetMapping("/films")
    public Collection<Film> returnFilms() {
        Collection<Film> filmList = films.values();
        return filmList;
    }

    private boolean filmValidation (Film film) throws ValidationException {
        var b = true;
        if  (film.getName() == null || film.getName().isBlank()) {
            b = false;
            throw new ValidationException("Название не может бытьпустым");
        }
        if (film.getDescription().length() > 200) {
            b = false;
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            b = false;
            throw new ValidationException("Ошибочная дата релиза");
        }
        if (film.getDuration() <= 0) {
            b = false;
            throw new ValidationException("продолжительность фильма должна быть положительной");
        }
        return b;
    }
}
