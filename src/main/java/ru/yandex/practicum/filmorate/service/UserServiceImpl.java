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
    public void addFriend(long userIds, long friendIds) {
        User user = findById(userIds);
        User friend = findById(friendIds);
        user.addFriend(friendIds);
        friend.addFriend(userIds);
        log.info("Added friend {}.", friend.getLogin());
    }

    @Override
    public void deleteFriend(long userIds, long friendIds) {
        User user = findById(userIds);
        User friend = findById(friendIds);
        user.removeFriend(friendIds);
        log.info("Delete friend {}.", friend.getLogin());
    }

    @Override
    public List<User> getFriends(long userIds) {
        log.info("Get a list of friends.");
        return findById(userIds).getFiends().stream().map(this::findById).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long thisFriendIds, long otherFriendIds) {
        log.info("Get a common list of friends.");
        List<Long> friendsId1 = findById(thisFriendIds).getFiends();
        List<Long> friendsId2 = findById(otherFriendIds).getFiends();
        friendsId1.retainAll(friendsId2);
        return friendsId1.stream()
                .map(this::findById)
                .collect(Collectors.toList());
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