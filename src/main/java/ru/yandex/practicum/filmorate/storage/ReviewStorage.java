package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> addReview(Review review);

    Optional<Review> updateReview(Review review);

    Optional<Review> findReviewById(int id);

    void deleteReviewById(int id);

    boolean isPresent(int id);

    Collection<Review> findReviewsByFilmId(int filmId, int count);

    Collection<Review> findAllReviews(int count);

    void addLikeToReview(int id, int userId);

    void addDislikeToReview(int id, int userId);

    void deleteDislikeFromReview(int id, int userId);

    void deleteLikeFromReview(int id, int userId);
}
