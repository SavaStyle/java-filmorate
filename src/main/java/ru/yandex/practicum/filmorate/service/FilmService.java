package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.util.sortByEnum;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.impl.FeedDbStorage.*;

@Component
@RequiredArgsConstructor
@Data
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesStorage likesStorage;
    private final FeedStorage feedStorage;
    private final DirectorStorage directorStorage;
    private final FilmDirectorStorage filmDirectorStorage;


    public Film addNewFilm(Film film) {
        filmValidation(film);
        filmStorage.addNewFilm(film);
        filmDirectorStorage.updateDirectorsOfFilm(film);
        setFilmDirectors(List.of(film));
        return film;
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
        filmDirectorStorage.updateDirectorsOfFilm(film);
        setFilmDirectors(List.of(film));
        return filmStorage.updateFilm(film);
    }

    public void addLike(int filmId, int userId) throws NotFoundException {
        getFilmById(filmId);
        userStorage.getUserById(userId);
        likesStorage.addLike(filmId, userId);
        feedStorage.addFeed(userId, LIKE, ADD, filmId);
    }

    public void removeLike(int filmId, int userId) throws NotFoundException {
        filmStorage.isPresent(filmId);
        userStorage.isPresent(userId);
        likesStorage.removeLike(filmId, userId);
        feedStorage.addFeed(userId, LIKE, REMOVE, filmId);
    }

    public Optional<Film> getFilmById(Integer id) {
        if (!(filmStorage.isPresent(id))) {
            throw new NotFoundException("???????????????????????? ???? ????????????");
        }
        Film film = filmStorage.getFilmById(id).get();
        setFilmDirectors(List.of(film));
        return Optional.of(film);
    }

    public List<Film> getTop(int count, Integer genreId, Integer year) {
        List<Film> films = filmStorage.getPopularFilms(count, genreId, year);
        setFilmDirectors(films);
        return films;
    }

    public boolean filmValidation(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("???????????????? ???? ?????????? ????????????????????");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("???????????????????????? ?????????? ???????????????? ??? 200 ????????????????");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("?????????????????? ???????? ????????????");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("?????????????????????????????????? ???????????? ???????????? ???????? ??????????????????????????");
        }
        return true;
    }

    public void deleteFilmById(Integer id) {
        filmStorage.removeFilmById(id);
    }

    public Collection<Film> getFilmsOfDirector(int directorId, sortByEnum sortBy) {
        directorStorage.getById(directorId).orElseThrow(() -> {
            throw new NotFoundException("???????????????? ???? ????????????");
        });
        Collection<Film> films = filmStorage.getFilmsOfDirector(directorId, sortBy);
        setFilmDirectors(films);
        return films;
    }

    private void setFilmDirectors(Collection<Film> films) {
        List<Integer> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        Map<Integer, Set<Director>> directors = filmDirectorStorage.getDirectorsOfFilms(filmIds);
        for (Film film : films) {
            film.setDirectors(directors.getOrDefault(film.getId(), new HashSet<>()));
        }
    }

    public Collection<Film> getAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms();
        setFilmDirectors(films);
        return films;
    }

    public Collection<Film> getCommonFilms(int userId, int friendId) {
        Collection<Film> films = filmStorage.getCommonFilms(userId, friendId);
        setFilmDirectors(films);
        return films;
    }


    public List<Film> search(String query, String[] by) {
        List<Film> films = filmStorage.search(query, by);
        setFilmDirectors(films);
        return films;
    }
}
