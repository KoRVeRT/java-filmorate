package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private long id;
    private final HashMap<Long, User> users = new HashMap<>();

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(long id) {
        return users.get(id);
    }

    public User create(User user) {
        user.setId(++id);
        users.put(id, user);
        log.info("Add User: {}", user);
        return user;
    }

    public User update(User user) {
        users.put(user.getId(), user);
        log.info("Update User: {}", user);
        return user;
    }
}