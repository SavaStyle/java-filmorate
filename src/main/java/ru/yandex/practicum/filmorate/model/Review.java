package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Null;

@Getter
@Setter
@AllArgsConstructor
public class Review {
    private Integer id;
    private String content;
    @Null
    private Integer useful;
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
}
