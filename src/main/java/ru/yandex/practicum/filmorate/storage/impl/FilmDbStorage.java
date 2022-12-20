package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;


    @Override
    public Film addNewFilm(Film film) {
        String sqlQuery = "INSERT INTO FILMS(FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        filmGenreStorage.addGenreNewFilm(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        filmGenreStorage.updateGenreFilm(film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(int id) throws NotFoundException {
        String sqlQuery = "SELECT FILMS.*, MPA.* " +
                "FROM FILMS " +
                "JOIN MPA ON MPA.MPA_ID = FILMS.MPA_ID " +
                "WHERE FILMS.FILM_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!row.next()) {
            return Optional.empty();
        }
        Film film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
        return Optional.ofNullable(film);
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sqlQuery = "SELECT FILMS.*, m.* " +
                "FROM FILMS " +
                "JOIN MPA m ON m.MPA_ID = films.MPA_ID";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILMS.FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, m.MPA_ID, m.MPA_NAME " +
                "FROM FILMS " +
                "LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID " +
                "LEFT JOIN mpa m on m.MPA_ID = films.MPA_ID " +
                "GROUP BY FILMS.FILM_ID, LIKES.FILM_ID IN ( " +
                "SELECT FILM_ID " +
                "FROM LIKES) " +
                "ORDER BY COUNT(LIKES.FILM_ID) DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, count);
        return films;
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("film_id");
        String name = resultSet.getString("film_name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        int duration = resultSet.getInt("duration");
        Mpa mpa = new Mpa(resultSet.getInt("MPA_ID"), resultSet.getString("MPA_NAME"));
        List<Genre> genres = genreStorage.setGenresMakeFilm(id);
        return new Film(id, name, description, releaseDate, duration, mpa, genres);
    }

    @Override
    public boolean isPresent(int id) {
        String sqlQuery = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!filmRow.next()) {
            throw new NotFoundException("Пользователь не найден");
        } else {
            return true;
        }
    }

    @Override
    public List<Film> getRecommendations(int userID) {
        String sqlQuery =
                "    SELECT FILMS.*, MPA.MPA_NAME" +
                "    FROM FILMS" +
                "    INNER JOIN (SELECT FILM_ID" +
                "           FROM (SELECT COLLABORATORS_LIKES.USER_ID, COUNT(COLLABORATORS_LIKES.FILM_ID) AS MATCHES" +
                "                   FROM LIKES AS COLLABORATORS_LIKES" +
                "                   INNER JOIN LIKES AS USER_LIKES" +
                "                   ON COLLABORATORS_LIKES.FILM_ID = USER_LIKES.FILM_ID" +
                "                   WHERE USER_LIKES.USER_ID = ? AND COLLABORATORS_LIKES.USER_ID <> ? " +
                "                   GROUP BY COLLABORATORS_LIKES.USER_ID" +
                "                   ORDER BY MATCHES DESC" +
                "                   LIMIT 1) AS COLLABORATOR" +
                "           INNER JOIN LIKES AS COLLABORATOR_FILMS" +
                "           ON COLLABORATOR.USER_ID = COLLABORATOR_FILMS.USER_ID" +
                "           WHERE COLLABORATOR_FILMS.FILM_ID NOT IN (SELECT FILM_ID " +
                "                   FROM LIKES " +
                "                   WHERE USER_ID = ?)) AS RECOMMENDED" +
                "   ON RECOMMENDED.FILM_ID = FILMS.FILM_ID" +
                "   LEFT JOIN MPA" +
                "   ON MPA.MPA_ID = FILMS.MPA_ID" +
                "   ORDER BY FILMS.FILM_ID";
        return jdbcTemplate.query(sqlQuery, this::makeFilm, userID, userID, userID);
    }
}
