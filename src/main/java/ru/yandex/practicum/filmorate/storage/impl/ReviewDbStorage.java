package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Primary
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Review> addReview(Review review) {
        String sqlQuery = "INSERT INTO REVIEWS(CONTENT, USEFUL, IS_POSITIVE, USER_ID, FILM_ID) values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"REVIEW_ID"});
            stmt.setString(1, review.getContent());
            stmt.setInt(2, 0);
            stmt.setBoolean(3, review.getIsPositive());
            stmt.setInt(4, review.getUserId());
            stmt.setInt(5, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return findReviewById(review.getId());
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        String sqlQuery = "UPDATE REVIEWS SET CONTENT = ?, USEFUL = ?, IS_POSITIVE = ?, USER_ID = ?, FILM_ID = ? WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, review.getContent(), review.getUseful(), review.getIsPositive(), review.getUserId(), review.getFilmId(), review.getId());
        return findReviewById(review.getId());
    }

    @Override
    public Optional<Review> findReviewById(int id) {
        String sqlQuery = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
        SqlRowSet rowReviewSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!rowReviewSet.next()) {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, ReviewDbStorage::makeReview, id));
    }


    @Override
    public void deleteReviewById(int id) {
        String sqlQuery = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    static Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("REVIEW_ID");
        String content = rs.getString("CONTENT");
        int useful = rs.getInt("USEFUL");
        boolean isPositive = rs.getBoolean("IS_POSITIVE");
        int userId = rs.getInt("USER_ID");
        int filmId = rs.getInt("FILM_ID");
        return new Review(id, content, useful, isPositive, userId, filmId);
    }

    @Override
    public Collection<Review> findReviewsByFilmId(int filmId, int count) {
        String sqlQuery = "SELECT * FROM REVIEWS WHERE FILM_ID = ? " +
                "GROUP BY REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, ReviewDbStorage::makeReview, filmId, count);
    }

    @Override
    public Collection<Review> findAllReviews(int count) {
        String sqlQuery = "SELECT * FROM REVIEWS " +
                "GROUP BY REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, ReviewDbStorage::makeReview, count);
    }


    @Override
    public boolean isPresent(int id) {
        String sqlQuery = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
        SqlRowSet reviewRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!reviewRow.next()) {
            throw new NotFoundException("Отзыв не найден");
        } else {
            return true;
        }
    }
}