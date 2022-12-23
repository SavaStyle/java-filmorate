package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping("/films")
    public Film addNewFilm(@RequestBody Film film) {
        return filmService.addNewFilm(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/films")
    public Collection<Film> returnFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Optional<Film> getFilmById(@PathVariable String id) {
        return filmService.getFilmById(Integer.valueOf(id));
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLike(@PathVariable String id, @PathVariable String userId) throws NotFoundException {
        filmService.addLike(Integer.parseInt(id), Integer.parseInt(userId));
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void removeLike(@PathVariable String id, @PathVariable String userId) throws NotFoundException {
        filmService.removeLike(Integer.parseInt(id), Integer.parseInt(userId));
    }

    @GetMapping("/films/popular")
    public Collection<Film> getTop(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count,
                                   @RequestParam(value = "genreId", required = false) Integer genreId,
                                   @RequestParam(value = "year", required = false) Integer year) {
        return filmService.getTop(count, genreId, year);
    }

    @DeleteMapping("/films/{id}")
    public void deleteFilmById(@PathVariable String id) {
        filmService.deleteFilmById(Integer.valueOf(id));
    }

    @GetMapping("/films/director/{directorId}")
    public Collection<Film> getFilmsOfDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getFilmsOfDirector(directorId, sortBy);
    }

    @GetMapping("/films/common")
    public Collection<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
 
    @GetMapping("/films/search")
    public Collection<Film> search(@RequestParam(value = "query", required = false) String query,
                                   @RequestParam(value = "by", required = false) String[] fields) {
         return filmService.search(query, fields);
    }
}