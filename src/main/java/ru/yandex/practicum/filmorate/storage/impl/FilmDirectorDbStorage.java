package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmDirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmDirectorDbStorage implements FilmDirectorStorage {
    JdbcTemplate jdbcTemplate;

    FilmDirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void updateDirectorsOfFilm(Film film) {
        jdbcTemplate.update("delete from FILMS_DIRECTORS where FILM_ID=?", film.getId());
        if (film.getDirectors() != null && film.getDirectors().size() > 0) {
            jdbcTemplate.batchUpdate("merge into FILMS_DIRECTORS(FILM_ID, DIRECTOR_ID) " +
                            "key(FILM_ID, DIRECTOR_ID) values (?, ?)",
                    film.getDirectors(),
                    100,
                    (PreparedStatement ps, Director director) -> {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, director.getId());
                    }
            );
        }
    }

    @Override
    public Map<Integer, Set<Director>> getDirectorsOfFilms(List<Integer> filmIds) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        SqlParameterSource parameters = new MapSqlParameterSource("filmIds", filmIds);

        String directorsSql = "select FD.FILM_ID, FD.DIRECTOR_ID, D.DIRECTOR_NAME " +
                "from FILMS_DIRECTORS as FD " +
                "join DIRECTORS as D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                "where FD.FILM_ID in (:filmIds)";
        return namedParameterJdbcTemplate.query(directorsSql,
                parameters,
                this::resultsToMap);
    }

    private Map<Integer, Set<Director>> resultsToMap(ResultSet resultSet) throws SQLException {
        HashMap<Integer, Set<Director>> queryResults = new HashMap<>();
        while (resultSet.next()) {
            int filmId = resultSet.getInt("FILM_ID");
            int directorId = resultSet.getInt("DIRECTOR_ID");
            String directorName = resultSet.getString("DIRECTOR_NAME");
            if (!queryResults.containsKey(filmId)) {
                queryResults.put(filmId, new HashSet<>());
            }
            queryResults.get(filmId).add(new Director(directorId, directorName));
        }
        return queryResults;
    }
}
