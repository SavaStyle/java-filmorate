package ru.yandex.practicum.filmorate.storage;

public interface ReviewLikeStorage {
    void addLikeToReview(int id, int userId);

    void addDislikeToReview(int id, int userId);

    void deleteDislikeFromReview(int id, int userId);

    void deleteLikeFromReview(int id, int userId);

    boolean isPresentLikeOrDislike(int id, int userId);

    boolean isPresentDislike(int id, int userId);

    boolean isPresentLike(int id, int userId);

}
