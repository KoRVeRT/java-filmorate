package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int id;
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping()
    public List<User> findAll() {
        log.info("Number of users: {}", users.values().size());
        return new ArrayList<>(users.values());
    }

    @PostMapping()
    public User create(@RequestBody User user) {
        checkValidation(user);
        id++;
        user.setId(id);
        log.info("Add User: {}", user);
        users.put(id, user);
        return user;
    }

    @PutMapping()
    public User update(@RequestBody User user) {
        checkValidation(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("No user to update.");
        }
        log.info("Update User: {}", user);
        users.put(user.getId(), user);
        return user;
    }

    private void checkValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("The email field is blank or does not contain an @.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Login field is empty or contains spaces.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("The date of birth cannot be in the future.");
        }
    }
}