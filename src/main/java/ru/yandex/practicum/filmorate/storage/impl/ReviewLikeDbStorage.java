package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;


@Repository
@RequiredArgsConstructor
public class ReviewLikeDbStorage implements ReviewLikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean isPresentLikeOrDislike(int id, int userId) {
        String sqlQuery = "SELECT * FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
        SqlRowSet reviewLike = jdbcTemplate.queryForRowSet(sqlQuery, id, userId);
        if (!reviewLike.next()) {
            return true;
        } else {
            throw new NotFoundException("Невозможно поставить лайк/дизлайк, пользователь уже поставил свою оценку.");
        }
    }

    @Override
    public boolean isPresentLike(int id, int userId) {
        String sqlQuery = "SELECT * FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ? AND IS_LIKE = ?";
        SqlRowSet reviewLike = jdbcTemplate.queryForRowSet(sqlQuery, id, userId, true);
        if (!reviewLike.next()) {
            throw new NotFoundException("Невозможно удалить лайк, так как такой оценки от пользователя нет.");
        } else {
            return true;
        }
    }

    @Override
    public boolean isPresentDislike(int id, int userId) {
        String sqlQuery = "SELECT * FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ? AND IS_LIKE = ?";
        SqlRowSet reviewLike = jdbcTemplate.queryForRowSet(sqlQuery, id, userId, false);
        if (!reviewLike.next()) {
            throw new NotFoundException("Невозможно удалить дизлайк, так как такой оценки от пользователя нет.");
        } else {
            return true;
        }
    }

    @Override
    public void addLikeToReview(int id, int userId) {
        String sqlQuery1 = "INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID, IS_LIKE) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery1, id, userId, true);
    }

    @Override
    public void addDislikeToReview(int id, int userId) {
        String sqlQuery1 = "INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID, IS_LIKE) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery1, id, userId, false);
    }

    @Override
    public void deleteDislikeFromReview(int id, int userId) {
        String sqlQuery1 = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID= ?";
        jdbcTemplate.update(sqlQuery1, id, userId);
    }

    @Override
    public void deleteLikeFromReview(int id, int userId) {
        String sqlQuery1 = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID= ?";
        jdbcTemplate.update(sqlQuery1, id, userId);
    }
}
