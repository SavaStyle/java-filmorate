package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Optional;

public interface LikesStorage {


    Optional<Film> addLike(int filmId, int userId) throws NotFoundException;

    Optional<Film> removeLike(int filmId, int userId) throws NotFoundException;
}
