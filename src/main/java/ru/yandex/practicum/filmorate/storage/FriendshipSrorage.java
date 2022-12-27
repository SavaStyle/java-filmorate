package ru.yandex.practicum.filmorate.storage;

public interface FriendshipSrorage {
    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);
}
