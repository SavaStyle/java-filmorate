package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Arrays;
import java.util.Collection;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenerTest {
    private final GenreService genre;

    @Test
    public void testGetAllGenres() {
        Collection<Genre> genreStorage = genre.findAll();
        Assertions.assertThat(genreStorage)
                .extracting(Genre::getName)
                .containsAll(Arrays.asList("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик"));}

    @Test
    public void testGetGenreById() {
        Genre genre1 = genre.getById(1);
        Assertions.assertThat(genre1)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

}
