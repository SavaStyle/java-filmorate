package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {
    DirectorService directorService;

    @Autowired
    DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public Collection<Director> getDirectors() {
        log.info("Запрошен список режиссёров");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable int id) {
        log.info("Запрошен режиссёр с id {}", id);
        return directorService.getDirector(id);
    }

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        director = directorService.addDirector(director);
        log.info("Добавлен режиссёр с id {}", director.getId());
        return director;
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        director = directorService.updateDirector(director);
        log.info("Обновлён режиссёр с id {}", director.getId());
        return director;
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable int id) {
        log.info("Удаление режиссёра с id {}", id);
        directorService.removeDirector(id);
    }
}
