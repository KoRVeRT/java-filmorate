package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
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
    public void remove(long userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User id: " + userId + " not found.");
        }
        userStorage.remove(userId);
        log.info("Deleted user");
    }

    @Override
    public User findById(long id) {
        log.info("Get user with id: {}", id);
        if (!userStorage.containsUser(id)) {
            throw new NotFoundException("User id: " + id + " not found.");
        }
        return userStorage.findById(id);

    }

    @Override
    public void addFriend(long userId, long friendId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User id: " + userId + " not found.");
        }
        if (!userStorage.containsUser(friendId)) {
            throw new NotFoundException("User id: " + friendId + " not found.");
        }
        userStorage.addFriend(userId, friendId);
        log.info("Added friend: {}.", friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User id: " + userId + " not found.");
        }
        if (!userStorage.containsUser(friendId)) {
            throw new NotFoundException("User id: " + friendId + " not found.");
        }
        userStorage.removeFriend(userId, friendId);
        log.info("Delete friend: {}.", friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User id: " + userId + " not found.");
        }
        log.info("Get a list of friends.");
        return userStorage.getFriends(userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User id: " + userId + " not found.");
        }
        if (!userStorage.containsUser(otherUserId)) {
            throw new NotFoundException("User id: " + otherUserId + " not found.");
        }
        log.info("Get a common list of friends.");
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Created name from login.");
        }
    }
}