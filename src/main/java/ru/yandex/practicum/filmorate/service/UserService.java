package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService implements CommonUserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
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
        return userStorage.update(user);
    }

    @Override
    public User findById(long id) {
        log.info("Get user with id: {}", id);
        validateId(id);
        User user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException("User id: " + id + " not found.");
        }
        return user;
    }

    @Override
    public void addFriend(long id, long friendId) {
        User user = findById(id);
        User friend = findById(friendId);
        user.addFriend(friendId);
        friend.addFriend(id);
        log.info("Added friend {}.", friend);
    }

    @Override
    public void deleteFriend(long id, long friendId) {
        User user = findById(id);
        User friend = findById(friendId);
        if (!user.containsFriend(friendId)) {
            log.warn("Friend: " + friend.getLogin() + " is not in friends.");
            return;
        }
        user.removeFriend(friendId);
    }

    @Override
    public List<User> getFriends(long id) {
        log.info("Get a list of friends.");
        return userStorage.findById(id).getFiends().stream().map(userStorage::findById).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        log.info("Get a common list of friends.");
        List<Long> friendsId1 = userStorage.findById(id).getFiends();
        List<Long> friendsId2 = userStorage.findById(otherId).getFiends();
        friendsId1.retainAll(friendsId2);
        return friendsId1.stream().map(userStorage::findById).collect(Collectors.toList());
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