package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;


@Repository
@RequiredArgsConstructor
public class ReviewLikeDbStorage implements ReviewLikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLikeToReview(int id, int userId) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        String sqlQuery1 = "INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery1, id, userId);
    }

    @Override
    public void addDislikeToReview(int id, int userId) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        String sqlQuery1 = "INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery1, id, userId);
    }

    @Override
    public void deleteDislikeFromReview(int id, int userId) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        String sqlQuery1 = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID= ?";
        jdbcTemplate.update(sqlQuery1, id, userId);
    }

    @Override
    public void deleteLikeFromReview(int id, int userId) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        String sqlQuery1 = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID= ?";
        jdbcTemplate.update(sqlQuery1, id, userId);
    }
}
