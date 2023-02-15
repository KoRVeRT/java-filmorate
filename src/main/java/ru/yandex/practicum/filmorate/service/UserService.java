package ru.yandex.practicum.filmorate.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User create(User user);

    User update(User user);

    User findById(long id);

    void addFriend(long userIds, long friendIds);

    void deleteFriend(long userIds, long friendIds);

    List<User> getFriends(@PathVariable long userIds);

    List<User> getCommonFriends(@PathVariable long thisFriendIds, @PathVariable long otherFriendIds);
}