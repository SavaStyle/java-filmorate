package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.objectweb.asm.Type;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UnsupportedActionException;
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
    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        List<Object> args = new LinkedList<>();
        List<Integer> argTypes = new ArrayList<>();
        if (genreId != null) {
            args.add(genreId);
            argTypes.add(Type.INT);
        }
        if (year != null) {
            args.add(year);
            argTypes.add(Type.INT);
        }
        args.add(count);
        argTypes.add(Type.INT);

        String sqlQuery =
                "   SELECT FILMS.*, MPA.MPA_NAME" +
                        "   FROM FILMS" +
                        "   LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) AS RATING" +
                        "       FROM LIKES" +
                        "       GROUP BY FILM_ID) AS FILMS_RATING" +
                        "   ON FILMS.FILM_ID=FILMS_RATING.FILM_ID" +
                        "   LEFT JOIN MPA" +
                        "   ON MPA.MPA_ID = FILMS.MPA_ID" +
                        (genreId != null ?
                                "   LEFT JOIN FILM_GENRE" +
                                        "   ON FILM_GENRE.FILM_ID = FILMS.FILM_ID" +
                                        "   WHERE FILM_GENRE.GENRE_ID = ?" : "") +
                        (year != null ? (genreId == null ? " WHERE" : " AND") +
                                "       EXTRACT(YEAR FROM FILMS.RELEASE_DATE) = ?" : ""
                        ) +
                        "   ORDER BY FILMS_RATING.RATING DESC NULLS LAST LIMIT ?";

        List<Film> films = jdbcTemplate.query(sqlQuery,
                args.toArray(),
                argTypes.stream().mapToInt(Integer::intValue).toArray(),
                this::makeFilm);

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
        return new Film(id, name, description, releaseDate, duration, mpa, genres, new HashSet<>());
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
    public void removeFilmById(int id) {
        String sql = "delete from FILMS where FILM_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Collection<Film> getFilmsOfDirector(int directorId, String sortBy) {
        String sql;
        switch (sortBy.toLowerCase()) {
            case "likes":
                sql = "select F.FILM_ID, F.FILM_NAME, F.DESCRIPTION, F.DURATION, F.RELEASE_DATE, F.MPA_ID, M.MPA_NAME, count(L.USER_ID) as RAITING " +
                        "from FILMS as F " +
                        "join MPA as M on F.MPA_ID = M.MPA_ID " +
                        "left join LIKES as L on F.FILM_ID = L.FILM_ID " +
                        "join FILMS_DIRECTORS as FD on F.FILM_ID = FD.FILM_ID " +
                        "where FD.DIRECTOR_ID = ? " +
                        "group by F.FILM_ID, F.FILM_NAME, F.DESCRIPTION, F.DURATION, F.RELEASE_DATE, F.MPA_ID, M.MPA_NAME " +
                        "order by RAITING desc;";
                break;
            case "year":
                sql = "select F.FILM_ID, F.FILM_NAME, F.DESCRIPTION, F.DURATION, F.RELEASE_DATE, F.MPA_ID, M.MPA_NAME, count(L.USER_ID) as RAITING " +
                        "from FILMS as F " +
                        "join MPA as M on F.MPA_ID = M.MPA_ID " +
                        "left join LIKES as L on F.FILM_ID = L.FILM_ID " +
                        "join FILMS_DIRECTORS as FD on F.FILM_ID = FD.FILM_ID " +
                        "where FD.DIRECTOR_ID = ? " +
                        "group by F.FILM_ID, F.FILM_NAME, F.DESCRIPTION, F.DURATION, F.RELEASE_DATE, F.MPA_ID, M.MPA_NAME " +
                        "order by extract(year from F.RELEASE_DATE)";
                break;
            default:
                throw new UnsupportedActionException("Неподдерживаемый параметр сортировки");
        }
        return jdbcTemplate.query(sql, this::makeFilm, directorId);
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

    @Override
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        String sql = "select F.*, M.MPA_NAME " +
                "from FILMS as F, LIKES as L1, LIKES as L2 " +
                "join MPA as M on F.MPA_ID = M.MPA_ID " +
                "join (select FILM_ID, count(USER_ID) as LIKES from LIKES group by FILM_ID) as RAITINGS " +
                "on F.FILM_ID = RAITINGS.FILM_ID " +
                "where F.FILM_ID = L1.FILM_ID and L1.USER_ID = ? and F.FILM_ID = L2.FILM_ID and L2.USER_ID = ? " +
                "order by RAITINGS.LIKES desc;";
        return jdbcTemplate.query(sql, this::makeFilm, userId, friendId);
    }

    @Override
    public List<Film> search(String query, String[] by) {
        if (query != null && !query.isEmpty()) {
            query = "%" + query + "%";
        }
        if (query == null || query.isEmpty()) {
            String sqlQuery =
                    "   SELECT FILMS.*, MPA.MPA_NAME" +
                            "   FROM FILMS" +
                            "   LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) AS RATING" +
                            "       FROM LIKES" +
                            "       GROUP BY FILM_ID) AS FILMS_RATING" +
                            "   ON FILMS.FILM_ID=FILMS_RATING.FILM_ID" +
                            "   LEFT JOIN MPA" +
                            "   ON MPA.MPA_ID = FILMS.MPA_ID" +
                            "   ORDER BY FILMS_RATING.RATING DESC NULLS LAST";
            return jdbcTemplate.query(sqlQuery, this::makeFilm);
        } else if (List.of(by).contains("director") && List.of(by).contains("title")) {
            String sqlQuery =
                    "   SELECT FILMS.*, MPA.MPA_NAME" +
                            "   FROM FILMS" +
                            "   LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) AS RATING" +
                            "       FROM LIKES" +
                            "       GROUP BY FILM_ID) AS FILMS_RATING" +
                            "   ON FILMS.FILM_ID=FILMS_RATING.FILM_ID" +
                            "   LEFT JOIN MPA ON MPA.MPA_ID = FILMS.MPA_ID" +
                            "   LEFT JOIN FILMS_DIRECTORS ON FILMS.FILM_ID = FILMS_DIRECTORS.FILM_ID" +
                            "   LEFT JOIN DIRECTORS ON FILMS_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID" +
                            "   WHERE DIRECTORS.DIRECTOR_NAME ILIKE ? OR FILMS.FILM_NAME ILIKE ?" +
                            "   ORDER BY FILMS_RATING.RATING DESC NULLS LAST";
            return jdbcTemplate.query(sqlQuery, this::makeFilm, query, query);
        } else if (List.of(by).contains("director")) {
            String sqlQuery =
                    "   SELECT FILMS.*, MPA.MPA_NAME" +
                            "   FROM FILMS" +
                            "   LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) AS RATING" +
                            "       FROM LIKES" +
                            "       GROUP BY FILM_ID) AS FILMS_RATING" +
                            "   ON FILMS.FILM_ID=FILMS_RATING.FILM_ID" +
                            "   LEFT JOIN MPA ON MPA.MPA_ID = FILMS.MPA_ID" +
                            "   LEFT JOIN FILMS_DIRECTORS ON FILMS.FILM_ID = FILMS_DIRECTORS.FILM_ID" +
                            "   LEFT JOIN DIRECTORS ON FILMS_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID" +
                            "   WHERE DIRECTORS.DIRECTOR_NAME ILIKE ?" +
                            "   ORDER BY FILMS_RATING.RATING DESC NULLS LAST";
            return jdbcTemplate.query(sqlQuery, this::makeFilm, query);
        } else {
            String sqlQuery =
                    "   SELECT FILMS.*, MPA.MPA_NAME" +
                            "   FROM FILMS" +
                            "   LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) AS RATING" +
                            "       FROM LIKES" +
                            "       GROUP BY FILM_ID) AS FILMS_RATING" +
                            "   ON FILMS.FILM_ID=FILMS_RATING.FILM_ID" +
                            "   LEFT JOIN MPA ON MPA.MPA_ID = FILMS.MPA_ID" +
                            "   LEFT JOIN FILMS_DIRECTORS ON FILMS.FILM_ID = FILMS_DIRECTORS.FILM_ID" +
                            "   LEFT JOIN DIRECTORS ON FILMS_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID" +
                            "   WHERE FILMS.FILM_NAME ILIKE ?" +
                            "   ORDER BY FILMS_RATING.RATING DESC NULLS LAST";
            return jdbcTemplate.query(sqlQuery, this::makeFilm, query);
        }
    }
}
