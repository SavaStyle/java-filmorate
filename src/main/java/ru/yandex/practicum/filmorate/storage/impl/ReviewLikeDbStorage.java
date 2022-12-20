package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewLikeDbStorage implements ReviewLikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final ReviewStorage reviewStorage;

    @Override
    public void addLikeToReview(int id, int userId) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        reviewStorage.findReviewById(id);
    }

    @Override
    public void addDislikeToReview(int id, int userId) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        reviewStorage.findReviewById(id);
    }

    @Override
    public void deleteDislikeFromReview(int id, int userId) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void deleteLikeFromReview(int id, int userId) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }
}
