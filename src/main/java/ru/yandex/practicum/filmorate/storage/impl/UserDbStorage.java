package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    static User makeUser(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("user_email");
        String login = rs.getString("user_login");
        String name = rs.getString("user_name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }

    @Override
    public User addNewUser(User user) {
        String sqlQuery = "insert into USERS(user_email, user_login, user_name, birthday) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            final LocalDate birthday = user.getBirthday();
            if (birthday == null) {
                stmt.setNull(4, Types.DATE);
            } else {
                stmt.setDate(4, Date.valueOf(birthday));
            }
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE USERS SET USER_EMAIL = ?, USER_LOGIN= ?, USER_NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        String sqlQuery = "select * from USERS";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::makeUser);
    }

    @Override
    public Optional<User> getUserById(int id) {
        String sqlQuery = "select * from USERS where USER_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!row.next()) {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, UserDbStorage::makeUser, id));
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sqlQuery = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sqlQuery = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getFriendsById(int id) {
        String sqlQuery = "SELECT USERS.USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, BIRTHDAY " +
                "FROM USERS " +
                "LEFT JOIN FRIENDSHIP f on USERS.USER_ID = f.FRIEND_ID " +
                "WHERE f.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::makeUser, id);
    }

    @Override
    public List<User> getCommonFriends(int u, int f) {
        String sqlQuery = "SELECT U.USER_ID, u.USER_EMAIL, u.USER_LOGIN, u.USER_NAME, u.BIRTHDAY " +
                "from users u, friendship f1, friendship f2 " +
                " where f1.USER_ID = ? " +
                "and f2.USER_ID = ? " +
                "and f1.FRIEND_ID = f2.FRIEND_ID " +
                "and f1.FRIEND_ID = u.USER_ID";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::makeUser, u, f);
    }

    @Override
    public boolean isPresent(int id) {
        String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!userRows.next()) {
            throw new NotFoundException("Пользователь не найден");
        } else {
            return true;
        }
    }
}
