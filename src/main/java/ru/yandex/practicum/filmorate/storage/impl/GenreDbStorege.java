package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@Repository
public class GenreDbStorege implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    static Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("GENRE_ID");
        String name = resultSet.getString("GENRE_NAME");
        return new Genre(id, name);
    }

    @Override
    public Collection<Genre> findAll() {
        String sqlQuery = "SELECT * FROM GENRE";
        return jdbcTemplate.query(sqlQuery, GenreDbStorege::makeGenre);
    }

    @Override
    public Optional<Genre> getById(int id) {
        String sqlQuery = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!genreRows.next()) {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, GenreDbStorege::makeGenre, id));
    }

    @Override
    public  List<Genre> setGenresMakeFilm(int id) {
        String sqlQuery = "SELECT GENRE.GENRE_ID, GENRE_NAME " +
                "FROM GENRE " +
                "LEFT JOIN FILM_GENRE FG on GENRE.GENRE_ID = FG.GENRE_ID " +
                "WHERE FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, GenreDbStorege::makeGenre, id);
    }
}
