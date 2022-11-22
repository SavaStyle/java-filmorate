package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Data
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    public void addLike(int filmId, int userId) throws NotFoundException {
        Film film = filmStorage.getFilm(filmId);
        if (film.getLike().add((long) userId)) {
            System.out.println("Лайк добавлен");
        } else {
            System.out.println("Ошибка добавления лайка");
        }
    }

    public void removeLike(int filmId, int userId) throws NotFoundException {
        Film film = filmStorage.getFilm(filmId);
        if (userStorage.getUser(userId) != null && film.getLike().remove((long) userId)) {
            System.out.println("Лайк пользователя " + userId + " удален");
        } else {
            System.out.println("Пользователь не ставил лайк этому фильму");
        }

    }

    public Collection<Film> getTop(int count) {
        return filmStorage.returnFilms()
                .stream()
                .sorted((o1, o2) -> o2.getLike().size() - o1.getLike().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
