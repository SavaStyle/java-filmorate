package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {
    void addFeed(int userId, String eventType, String operation, int friendId);

    List<Feed> getFeed(int id);
}
