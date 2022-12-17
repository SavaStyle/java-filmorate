package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaDbStorage;

    public Collection<Mpa> findAll() {
        return mpaDbStorage.findAllMpa();
    }

    public Mpa getById(int id) {
        return mpaDbStorage.getMpaById(id).orElseThrow(() -> {
            throw new NotFoundException("Рейтинг не найден");
        });
    }
}
