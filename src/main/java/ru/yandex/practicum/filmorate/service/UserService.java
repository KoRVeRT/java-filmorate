package ru.yandex.practicum.filmorate.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User create(User user);

    User update(User user);

    void remove(User user);

    User findById(long id);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getFriends(@PathVariable long userId);

    List<User> getCommonFriends(@PathVariable long thisFriendId, @PathVariable long otherFriendId);
}