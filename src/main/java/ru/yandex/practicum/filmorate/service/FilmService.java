package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService implements CommonFilmService {
    private final FilmStorage filmStorage;
    private final CommonUserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, CommonUserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> findAll() {
        log.info("Get all films.");
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        findById(film.getId());
        return filmStorage.update(film);
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
    public void addLike(long id, long userId) {
        Film film = findById(id);
        User user = userService.findById(id);
        film.addLike(userId);
        log.info("Added like from user: {}.", user.getLogin());
    }

    @Override
    public void deleteLike(long id, long userId) {
        Film film = findById(id);
        User user = userService.findById(userId);
        film.removeLike(userId);
        log.info("Delete like from user: {}.", user.getLogin());
    }

    @Override
    public List<Film> findPopularMovies(long count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> Long.compare(f2.getLikesCount(), f1.getLikesCount()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateId(long id) {
        if (id <= 0) {
            throw new NotFoundException("Invalid id: " + id);
        }
    }
}