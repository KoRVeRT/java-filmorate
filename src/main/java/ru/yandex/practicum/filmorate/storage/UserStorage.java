package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(User user);

    User update(User user);

    void remove(User user);

    User findById(long id);

    void addFriend(User user, User friend);

    void removeFriend(User user, User friend);

    List<User> getFriends(@PathVariable User user);

    List<User> getCommonFriends(@PathVariable User user, @PathVariable User otherUser);
}