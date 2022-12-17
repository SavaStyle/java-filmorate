package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
@AllArgsConstructor
public class GenreService {

    private final GenreStorage genreDbStorage;

    public Collection<Genre> findAll() {
        return genreDbStorage.findAll();
    }

    public Genre getById(int id) {

        return genreDbStorage.getById(id).orElseThrow(() -> {
            throw new NotFoundException("Жанр не найден");
        });
    }
}
