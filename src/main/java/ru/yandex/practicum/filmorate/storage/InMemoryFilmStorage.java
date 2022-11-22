package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static ru.yandex.practicum.filmorate.util.Utils.getNextFilmId;

@Service
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film addNewFilm(Film film) throws ValidationException {
        if (filmValidation(film)) {
        }
        film.setId(getNextFilmId());
        film.setLike(new HashSet<>());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, NotFoundException {
        if (filmValidation(film)) {
            if (!(films.keySet().contains(film.getId()))) {
                throw new NotFoundException("фильм не известен");
            }
        }
        if (film.getLike() == null) {
            film.setLike(new HashSet<>());
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> returnFilms() {
        Collection<Film> filmList = films.values();
        return filmList;
    }

    @Override
    public Film getFilm(int id) throws NotFoundException {
        if (!(films.keySet().contains(id))) {
            throw new NotFoundException("фильм не найден");
        }
        return films.get(id);
    }

    @Override
    public boolean filmValidation(Film film) throws ValidationException {
        var b = true;
        if (film.getName() == null || film.getName().isBlank()) {
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
