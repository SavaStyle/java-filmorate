package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Optional<Review> addReview(Review review) {
        filmStorage.isPresent(review.getFilmId());
        userStorage.isPresent(review.getUserId());
        return reviewStorage.addReview(review);
    }

    public Optional<Review> updateReview(Review review) {
        reviewStorage.isPresent(review.getId());
        return reviewStorage.updateReview(review);
    }

    public Optional<Review> findReviewById(int id) {
        return Optional.ofNullable(reviewStorage.findReviewById(id).orElseThrow(() -> {
            throw new NotFoundException("Отзыв не найден");
        }));
    }

    public void deleteReviewById(int id) {
        reviewStorage.findReviewById(id).orElseThrow(() -> {
            throw new NotFoundException("Отзыв не найден");
        });
        reviewStorage.deleteReviewById(id);
    }

    public Collection<Review> findReviewsByFilmId(int filmId, int count) {
        filmStorage.isPresent(filmId);
        return reviewStorage.findReviewsByFilmId(filmId, count);
    }

    public Collection<Review> findAllReviews(int count) {
        return reviewStorage.findAllReviews(count);
    }

    public void addLikeToReview(int id, int userId) {
        userStorage.isPresent(userId);
        reviewStorage.isPresent(id);
        reviewLikeStorage.addLikeToReview(id, userId);
    }

    public void addDislikeToReview(int id, int userId) {
        userStorage.isPresent(userId);
        reviewStorage.isPresent(id);
        reviewLikeStorage.addDislikeToReview(id, userId);
    }

    public void deleteDislikeFromReview(int id, int userId) {
        userStorage.isPresent(userId);
        reviewStorage.isPresent(id);
        reviewLikeStorage.deleteDislikeFromReview(id, userId);
    }

    public void deleteLikeFromReview(int id, int userId) {
        userStorage.isPresent(userId);
        reviewStorage.isPresent(id);
        reviewLikeStorage.deleteLikeFromReview(id, userId);
    }
}
