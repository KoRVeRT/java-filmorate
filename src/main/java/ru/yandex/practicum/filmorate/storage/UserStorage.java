package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(User user);

    User update(User user);

    void remove(long userId);

    User findById(long id);

    void addFriend(long userId, long otherUserId);

    void removeFriend(long userId, long otherUserId);

    boolean containsUser(long userId);

    List<User> getFriends(long userId);

    List<User> getCommonFriends(long userId, long otherUserId);
}