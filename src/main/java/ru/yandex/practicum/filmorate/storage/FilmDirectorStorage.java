package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmDirectorStorage {
    void updateDirectorsOfFilm(Film film);

    Map<Integer, Set<Director>> getDirectorsOfFilms(List<Integer> filmIds);
}
