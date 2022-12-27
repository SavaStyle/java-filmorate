package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Collection<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    public Director getDirector(int id) {
        return directorStorage.getById(id).orElseThrow(() -> {
            throw new NotFoundException("Режиссёр не найден");
        });
    }

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director).orElseThrow(() -> {
            throw new NotFoundException("Режиссёр не найден");
        });
    }

    public void removeDirector(int id) {
        directorStorage.removeDirector(id);
    }
}
