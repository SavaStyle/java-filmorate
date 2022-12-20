package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Collection<Director> getDirectors();

    Optional<Director> getById(int id);

    Director addDirector(Director director);

    Optional<Director> updateDirector(Director director);

    void removeDirector(int id);
}
