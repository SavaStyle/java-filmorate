package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Data
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addNewFilm(Film film) {
        try {
            filmValidation(film);
            filmStorage.addNewFilm(film);
            return film;
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public Film updateFilm(Film film) {
        filmValidation(film);
        filmStorage.isPresent(film.getId());
        List<Genre> listWithoutDuplicates = new ArrayList<>();
        if (film.getGenres() != null) {
            listWithoutDuplicates =
                    film.getGenres().stream().distinct().collect(Collectors.toList());
        }
        film.setGenres(listWithoutDuplicates);
        return filmStorage.updateFilm(film);
    }

    public void addLike(int filmId, int userId) throws NotFoundException {
        getFilmById(filmId);
        userStorage.getUserById(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) throws NotFoundException {
        filmStorage.isPresent(filmId);
        userStorage.isPresent(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public Optional<Film> getFilmById(Integer id) {
        if (!(filmStorage.isPresent(id))) {
            throw new NotFoundException("Пользователь не найден");
        }
        return filmStorage.getFilmById(id);
    }

    public List<Film> getTop(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public boolean filmValidation(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может бытьпустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Ошибочная дата релиза");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("продолжительность фильма должна быть положительной");
        }
        return true;
    }
}
