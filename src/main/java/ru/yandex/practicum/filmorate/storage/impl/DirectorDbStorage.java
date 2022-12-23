package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Director> getDirectors() {
        String sql = "select * from DIRECTORS order by DIRECTOR_ID";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));

    }

    @Override
    public Optional<Director> getById(int id) {
        String sql = "select * from DIRECTORS where DIRECTOR_ID=?";
        List<Director> queryResult = jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), id);
        if (queryResult.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(queryResult.get(0));
    }

    @Override
    public Director addDirector(Director director) {
        String sql = "insert into DIRECTORS(DIRECTOR_NAME) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"DIRECTOR_ID"});
            statement.setString(1, director.getName());
            return statement;
        }, keyHolder);
        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        director.setId(id);
        return director;
    }

    @Override
    public Optional<Director> updateDirector(Director director) {
        String sql = "update DIRECTORS " +
                "set DIRECTOR_NAME = ? " +
                "where DIRECTOR_ID = ?";
        int result = jdbcTemplate.update(sql,
                director.getName(),
                director.getId());
        if (result < 1) {
            return Optional.empty();
        }
        return Optional.of(director);
    }

    @Override
    public void removeDirector(int id) {
        String sql = "delete from DIRECTORS where DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("director_id");
        String name = rs.getString("director_name");
        return new Director(id, name);
    }
}
