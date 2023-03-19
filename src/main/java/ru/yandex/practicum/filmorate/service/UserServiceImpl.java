package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> findAll() {
        log.info("Get all users");
        return userStorage.findAll();
    }

    @Override
    public User create(User user) {
        checkUserName(user);
        return userStorage.create(user);
    }

    @Override
    public User update(User user) {
        findById(user.getId());
        log.info("Update user");
        return userStorage.update(user);
    }

    @Override
    public void remove(User user) {
        findById(user.getId());
        userStorage.remove(user);
        log.info("Deleted user");
    }

    @Override
    public User findById(long id) {
        validateId(id);
        log.info("Get user with id: {}", id);
        try {
            return userStorage.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User not found: " + id);
        }
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        userStorage.addFriend(user, friend);
        log.info("Added friend: {}.", friend.getLogin());
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        userStorage.removeFriend(user, friend);
        log.info("Delete friend: {}.", friend.getLogin());
    }

    @Override
    public List<User> getFriends(long userId) {
        User user = findById(userId);
        log.info("Get a list of friends.");
        return userStorage.getFriends(user);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        User user = findById(userId);
        User otherUser = findById(otherUserId);
        log.info("Get a common list of friends.");
        return userStorage.getCommonFriends(user, otherUser);
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Created name from login.");
        }
    }

    private void validateId(long id) {
        if (id <= 0) {
            throw new NotFoundException("Invalid id: " + id);
        }
    }
}