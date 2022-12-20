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

    boolean isPresent(int id);

    void removeFilmById(int id);

    List<Film> getRecommendations(int userID);

    Collection<Film> getFilmsOfDirector(int directorId, String sortBy);

    List<Film> search(String query, String[] by);
}

