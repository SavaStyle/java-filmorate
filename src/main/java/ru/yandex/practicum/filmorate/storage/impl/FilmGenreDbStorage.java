package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.util.ArrayList;

@RequiredArgsConstructor
@Repository
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addGenreNewFilm(Film film) {
        if (film.getGenres() != null) {
            String genres = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(genres, film.getGenres(), film.getGenres().size(),
                    (ps, genre) -> {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, genre.getId());
                    });
        } else film.setGenres(new ArrayList<>());
    }

    @Override
    public void updateGenreFilm(Film film) {
        if (film.getGenres() != null) {
            String deleteGenres = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
            jdbcTemplate.update(deleteGenres, film.getId());
            String updateGenres = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            for (Genre g : film.getGenres()) {
                String checkDuplicate = "SELECT * FROM FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?";
                SqlRowSet checkRows = jdbcTemplate.queryForRowSet(checkDuplicate, film.getId(), g.getId());
                if (!checkRows.next()) {
                    jdbcTemplate.update(updateGenres, film.getId(), g.getId());
                }
            }
        } else film.setGenres(new ArrayList<>());
    }
}
