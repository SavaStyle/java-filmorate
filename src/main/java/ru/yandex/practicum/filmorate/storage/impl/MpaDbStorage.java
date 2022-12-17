package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    static Mpa makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("MPA_ID");
        String name = resultSet.getString("MPA_NAME");
        return new Mpa(id, name);
    }

    @Override
    public Collection<Mpa> findAllMpa() {
        String sqlQuery = "SELECT * FROM MPA";
        return jdbcTemplate.query(sqlQuery, MpaDbStorage::makeMpa);
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        String sqlQuery = "SELECT * FROM MPA WHERE MPA_ID = ?";
        SqlRowSet rowMpaSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!rowMpaSet.next()) {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, MpaDbStorage::makeMpa, id));
    }
}
