package ru.yandex.practicum.filmorate.util;

public class Utils {
    protected static int nextFilmId = 0;
    protected static int nextUserId = 0;

    public static int getNextFilmId() {
        return ++nextFilmId;
    }

    public static int getNextUserId() {
        return ++nextUserId;
    }

}
