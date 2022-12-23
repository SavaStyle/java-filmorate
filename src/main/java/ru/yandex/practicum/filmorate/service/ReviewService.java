package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
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
        reviewValidation(review);
        filmStorage.isPresent(review.getFilmId());
        userStorage.isPresent(review.getUserId());
        return reviewStorage.addReview(review);
    }

    public Optional<Review> updateReview(Review review) {
        reviewStorage.isPresent(review.getReviewId());
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
        reviewLikeStorage.isPresentLikeOrDislike(id, userId);
        reviewLikeStorage.addLikeToReview(id, userId);
    }

    public void addDislikeToReview(int id, int userId) {
        userStorage.isPresent(userId);
        reviewStorage.isPresent(id);
        reviewLikeStorage.isPresentLikeOrDislike(id, userId);
        reviewLikeStorage.addDislikeToReview(id, userId);
    }

    public void deleteDislikeFromReview(int id, int userId) {
        userStorage.isPresent(userId);
        reviewStorage.isPresent(id);
        reviewLikeStorage.isPresentDislike(id, userId);
        reviewLikeStorage.deleteDislikeFromReview(id, userId);
    }

    public void deleteLikeFromReview(int id, int userId) {
        userStorage.isPresent(userId);
        reviewStorage.isPresent(id);
        reviewLikeStorage.isPresentLike(id, userId);
        reviewLikeStorage.deleteLikeFromReview(id, userId);
    }

    public boolean reviewValidation(Review review) throws ValidationException {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Отзыв не может быть пустым");
        }
        if (review.getUserId() == null || review.getUserId() == 0) {
            throw new ValidationException("Пользователь должен быть указан");
        }
        if (review.getFilmId() == null || review.getFilmId() == 0) {
            throw new ValidationException("Фильм должен быть указан");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Тип отзыва должен быть указан");
        }
        return true;
    }
}
