package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;
import java.util.Optional;


@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;


    @PostMapping
    public Optional<Review> addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping()
    public Optional<Review> updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @GetMapping("/{id}")
    public Optional<Review> findReviewById(@PathVariable int id) {
        return reviewService.findReviewById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable int id) {
        reviewService.deleteReviewById(id);
    }

    @GetMapping()
    public Collection<Review> findReviewsByFilmId(@RequestParam(value = "filmId", defaultValue = "0", required = false) Integer filmId, @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        if (filmId != 0) {
            return reviewService.findReviewsByFilmId(filmId, count);
        } else {
            return reviewService.findAllReviews(count);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeFromReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.deleteDislikeFromReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.deleteLikeFromReview(id, userId);
    }
}
