package ru.yandex.practicum.filmorate.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface CommonUserService {
    List<User> findAll();

    User create(User user);

    User update(User user);

    User findById(long id);

    void addFriend(long id, long friendId);

    void deleteFriend(long id, long friendId);

    List<User> getFriends(@PathVariable long id);

    List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId);
}
