package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> findAll() {
        log.info("Get all films.");
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        log.info("Added film.");
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        log.info("Updated film");
        return filmStorage.update(film);
    }

    @Override
    public void remove(Film film) {
        filmStorage.remove(film);
        log.info("Deleted film");
    }

    @Override
    public Film findById(long id) {
        log.info("Get film with id: {}", id);
        validateId(id);
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException("Film id: " + id + " not found.");
        }
        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {
        Film film = findById(filmId);
        User user = userService.findById(userId);
        filmStorage.addLike(film, user);
        log.info("Added like from user: {}.", user.getLogin());
    }

    @Override
    public void removeLike(long filmId, long userId) {
        Film film = findById(filmId);
        User user = userService.findById(userId);
        filmStorage.removeLike(film, user);
        log.info("Delete like from user: {}.", user.getLogin());
    }

    @Override
    public List<Film> findPopularMovies(long count) {
        if (count <= 0) {
            throw new ValidationException(String.format("The count:%d of popular movies should be a positive number."
                    , count));
        }
        return filmStorage.findPopularMovies(count);
    }

    private void validateId(long id) {
        if (id <= 0) {
            throw new NotFoundException("Invalid id: " + id);
        }
    }
}