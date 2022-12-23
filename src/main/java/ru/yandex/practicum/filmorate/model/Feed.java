package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Feed {

    private Long timestamp;

    private Integer userId;

    private String eventType;

    private String operation;

    private Integer eventId;

    private Integer entityId;
}
