package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int id;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping()
    public List<Film> findAll() {
        log.info("Number of films: {}", films.values().size());
        return new ArrayList<>(films.values());
    }

    @PostMapping()
    public Film create(@RequestBody Film film) {
        checkValidation(film);
        id++;
        film.setId(id);
        log.info("Add Film: {}", film);
        films.put(id, film);
        return film;
    }

    @PutMapping()
    public Film update(@RequestBody Film film) {
        checkValidation(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("No film to update.");
        }
        log.info("Update Film: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    private void checkValidation(Film film) {
        int maxCharacterCount = 200;
        LocalDate moviesBirthday = LocalDate.of(1895, Month.DECEMBER, 28);
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("The title cannot be empty.");
        }
        if (film.getDescription().length() > maxCharacterCount) {
            throw new ValidationException("The maximum length of the description is more than 200 characters.");
        }
        if (film.getReleaseDate().isBefore(moviesBirthday)) {
            throw new ValidationException("The date of birth cannot be in the future.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("The duration of the film should be positive.");
        }
    }
}
