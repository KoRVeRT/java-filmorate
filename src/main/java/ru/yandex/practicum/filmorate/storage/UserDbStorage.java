package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"USER_ID"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            user.setId((Integer) keyHolder.getKey());
        } else {
            throw new ValidationException("User failed to create id.");
        }
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
        if (jdbcTemplate.update(sql, user.getId()) <= 0) {
            throw new NotFoundException("User not deleted");
        }
    }

    @Override
    public User findById(long id) {
        final String sql = "SELECT * FROM USERS WHERE USER_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::userMapRow, id);
        } catch (
                IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("User id not found");
        }
    }

    @Override
    public void addFriend(User user, User friend) {
        String sql = "insert into FRIENDS(USER_ID, FRIEND_ID) values (?, ?)";
        jdbcTemplate.update(sql, user.getId(), friend.getId());
    }

    @Override
    public void removeFriend(User user, User friend) throws DataIntegrityViolationException {
        String sql = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, user.getId(), friend.getId());
    }

    @Override
    public List<User> getFriends(User user) {
        final String sql =
                "SELECT * FROM USERS INNER JOIN FRIENDS ON USERS.USER_ID = FRIENDS.FRIEND_ID WHERE FRIENDS.USER_ID = ?";

        return jdbcTemplate.query(sql, this::userMapRow, user.getId());
    }

    @Override
    public List<User> getCommonFriends(User user, User otherUser) {
        final String sql =
                "SELECT * FROM USERS WHERE USER_ID IN (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ? " +
                        "AND FRIEND_ID IN (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?))";

        return jdbcTemplate.query(sql, this::userMapRow, user.getId(), otherUser.getId());
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