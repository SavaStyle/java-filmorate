package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class FeedDbStorage implements FeedStorage {
    public final static String FRIEND = "FRIEND";
    public final static String LIKE = "LIKE";
    public final static String REVIEW = "REVIEW";
    public final static String REMOVE = "REMOVE";
    public final static String ADD = "ADD";
    public final static String UPDATE = "UPDATE";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFeed(int userId, String eventType, String operation, int entityId) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("FEED").usingGeneratedKeyColumns("EVENT_ID");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("FEED_TIMESTAMP", timestamp);
        parameters.put("USER_ID", userId);
        parameters.put("EVENT_TYPE", eventType);
        parameters.put("OPERATION", operation);
        parameters.put("ENTITY_ID", entityId);
        jdbcInsert.execute(parameters);
    }

    @Override
    public List<Feed> getFeed(int id) {
        String sqlQuery = "SELECT * FROM FEED where USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::makeFeed, id);
    }

    private Feed makeFeed(ResultSet resultSet, int rowNum) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp("FEED_TIMESTAMP");
        Integer userId = resultSet.getInt("USER_ID");
        String eventType = resultSet.getString("EVENT_TYPE");
        String operation = resultSet.getString("OPERATION");
        Integer eventId = resultSet.getInt("EVENT_ID");
        Integer entityId = resultSet.getInt("ENTITY_ID");
        return new Feed(timestamp.getTime(), userId, eventType, operation, eventId, entityId);
    }
}