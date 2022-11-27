package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
@RequiredArgsConstructor
@Data
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(int userID, int friendId) throws NotFoundException {
        if ((userStorage.getUser(userID)) != null && (userStorage.getUser(friendId) != null)) {
            User user = userStorage.getUser(userID);
            User friend = userStorage.getUser(friendId);
            user.getFriends().add(friendId);
            friend.getFriends().add(userID);
        }
    }

    public void deleteFriend(int userID, int friendId) throws NotFoundException {
        if ((userStorage.getUser(userID)) != null && (userStorage.getUser(friendId) != null)) {
            User user = userStorage.getUser(userID);
            User friend = userStorage.getUser(friendId);
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userID);
        }
    }

    public List<User> getFriendsList(int userID) throws NotFoundException {
        List<User> friends = new ArrayList<>();
        User user = userStorage.getUser(userID);
        List<Integer> idList = new ArrayList<>(user.getFriends());
        Collections.sort(idList);
        for (Integer id : idList) {
            friends.add(userStorage.getUser(id));
        }
        return friends;
    }

    public List<User> getCommonFriends(int userID, int friendId) throws NotFoundException {
        Set<Integer> newSet = new HashSet<>(Collections.emptySet());
        newSet.addAll(userStorage.getUser(userID).getFriends());
        List<User> common = new ArrayList<>();
        for (Integer id : userStorage.getUser(friendId).getFriends()) {
            if (!newSet.add(id)) {
                common.add(userStorage.getUser(id));
            }
        }
        return common;
    }

}
