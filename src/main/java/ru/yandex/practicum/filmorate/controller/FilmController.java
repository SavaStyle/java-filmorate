package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping("/films")
    public Film addNewFilm(@RequestBody Film film) throws ValidationException {
        return filmService.getFilmStorage().addNewFilm(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@RequestBody Film film) throws ValidationException, NotFoundException {
        return filmService.getFilmStorage().updateFilm(film);
    }

    @GetMapping("/films")
    public Collection<Film> returnFilms() {
        return filmService.getFilmStorage().returnFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable String id) throws NotFoundException {
        return filmService.getFilmStorage().getFilm(Integer.parseInt(id));
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void updateFilm(@PathVariable String id, @PathVariable String userId) throws NotFoundException {
        filmService.addLike(Integer.parseInt(id), Integer.parseInt(userId));
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void removeLike(@PathVariable String id, @PathVariable String userId) throws NotFoundException {
        filmService.removeLike(Integer.parseInt(id), Integer.parseInt(userId));
    }

    @GetMapping("/films/popular")
    public Collection<Film> getTop(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getTop(count);
    }

}
