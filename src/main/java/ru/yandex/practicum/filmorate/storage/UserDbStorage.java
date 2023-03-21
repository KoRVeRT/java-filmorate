package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
@RequiredArgsConstructor()
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        final String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, this::userMapRow);
    }

    @Override
    public User create(User user) {
        final String sql = "INSERT INTO USERS(NAME, LOGIN, EMAIL, BIRTHDAY) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(psc -> {
            PreparedStatement stmt = psc.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId((Integer) keyHolder.getKey());
        return user;
    }

    @Override
    public User update(User user) {
        final String sql = "UPDATE USERS SET NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sql,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        return user;
    }

    @Override
    public void remove(User user) {
        final String sql = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sql, user.getId());


    }

    @Override
    public User findById(long id) {
        final String sql = "SELECT * FROM USERS WHERE USER_ID = ?";
            return jdbcTemplate.queryForObject(sql, this::userMapRow, id);
    }

    @Override
    public void addFriend(long userId, long otherUserId) {
        String sql = "insert into FRIENDS(USER_ID, FRIEND_ID) values (?, ?)";
        jdbcTemplate.update(sql, userId, otherUserId);
    }

    @Override
    public void removeFriend(long userId, long otherUserId) {
        String sql = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, userId, otherUserId);
    }

    @Override
    public List<User> getFriends(long userId) {
        final String sql =
                "SELECT * FROM USERS INNER JOIN FRIENDS ON USERS.USER_ID = FRIENDS.FRIEND_ID WHERE FRIENDS.USER_ID = ?";

        return jdbcTemplate.query(sql, this::userMapRow, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        final String sql =
                "SELECT * FROM USERS WHERE USER_ID IN (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ? " +
                        "AND FRIEND_ID IN (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?))";

        return jdbcTemplate.query(sql, this::userMapRow, userId, otherUserId);
    }

    @Override
    public boolean containsUser(long userId) {
        final String sql = "SELECT * FROM USERS WHERE USER_ID = ?";
        try {
            jdbcTemplate.queryForObject(sql, this::userMapRow, userId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private User userMapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("USER_ID"))
                .name(rs.getString("NAME"))
                .login(rs.getString("LOGIN"))
                .email(rs.getString("EMAIL"))
                .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                .build();
    }
}