package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addNewFilm(Film film) throws ValidationException;

    Film updateFilm(Film film) throws ValidationException, NotFoundException;

    Optional<Film> getFilmById(int id) throws NotFoundException;

    Collection<Film> getAllFilms();

    List<Film> getPopularFilms(int count);

    Optional<Film> addLike(int filmId, int userId) throws NotFoundException;

    Optional<Film> removeLike(int filmId, int userId) throws NotFoundException;

    boolean isPresent(int id);
}

