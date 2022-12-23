package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmGenreStorage {
    void addGenreNewFilm(Film film);

    void updateGenreFilm(Film film);
}
