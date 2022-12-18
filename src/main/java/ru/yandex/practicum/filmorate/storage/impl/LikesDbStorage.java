package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.util.Optional;

@AllArgsConstructor
@Repository
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;
    private final  FilmStorage filmStorage;

    @Override
    public Optional<Film> addLike(int filmId, int userId) throws NotFoundException {
        String sqlQuery = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public Optional<Film> removeLike(int filmId, int userId) throws NotFoundException {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID= ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return filmStorage.getFilmById(filmId);
    }
}
