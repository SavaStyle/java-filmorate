package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.Null;

@Getter
@Setter
@AllArgsConstructor
public class Review {
    @Min(1)
    private Integer reviewId;
    private String content;
    @Null
    private Integer useful;
    private Boolean isPositive;
    @Min(1)
    private Integer userId;
    @Min(1)
    private Integer filmId;
}
