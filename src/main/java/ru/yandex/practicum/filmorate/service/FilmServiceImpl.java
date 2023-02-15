package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public void addLike(long filmIds, long userIds) {
        Film film = findById(filmIds);
        User user = userService.findById(filmIds);
        film.addLike(userIds);
        log.info("Added like from user: {}.", user.getLogin());
    }

    @Override
    public void deleteLike(long filmIds, long userIds) {
        Film film = findById(filmIds);
        User user = userService.findById(userIds);
        film.removeLike(userIds);
        log.info("Delete like from user: {}.", user.getLogin());
    }

    @Override
    public List<Film> findPopularMovies(long count) {
        if (count <= 0) {
            throw new ValidationException(String.format("The count:%d of popular movies should be a positive number."
                    , count));
        }
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparing(Film::getLikesCount, Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateId(long id) {
        if (id <= 0) {
            throw new NotFoundException("Invalid id: " + id);
        }
    }
}