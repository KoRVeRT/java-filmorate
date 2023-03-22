package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, GenreStorage genreStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findAll() {
        log.info("Get all films.");
        final List<Film> films = filmStorage.findAll();
        films.forEach(film -> film.setGenres(genreStorage.findGenresForFilm(film.getId())));
        return films;
    }

    public Film create(Film film) {
        log.info("Added film.");
        filmStorage.create(film);
        return findById(film.getId());
    }

    public Film update(Film film) {
        log.info("Updated film");
        filmStorage.update(film);
        if (!filmStorage.containsFilm(film.getId())) {
            throw new NotFoundException("Film id: " + film.getId() + " not update.");
        }
        return findById(film.getId());
    }

    @Override
    public void remove(long filmId) {
        if (!filmStorage.containsFilm(filmId)) {
            throw new NotFoundException("Film id: " + filmId + " not found.");
        }
        filmStorage.remove(filmId);
        log.info("Deleted film");
    }

    @Override
    public Film findById(long id) {
        log.info("Get film with id: {}", id);
        if (!filmStorage.containsFilm(id)) {
            throw new NotFoundException("Film id: " + id + " not found.");
        }
        Film film = filmStorage.findById(id);
        film.setGenres(genreStorage.findGenresForFilm(id));
        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {
        if (!filmStorage.containsFilm(filmId)) {
            throw new NotFoundException("Film id: " + filmId + " not found.");
        }
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User id: " + userId + " not found.");
        }
        filmStorage.addLike(filmId, userId);
        log.info("Added like from user: {}.", userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        if (!filmStorage.containsFilm(filmId)) {
            throw new NotFoundException("Film id: " + filmId + " not found.");
        }
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User id: " + userId + " not found.");
        }
        filmStorage.removeLike(filmId, userId);
        log.info("Delete like from user: {}.", userId);
    }

    @Override
    public List<Film> findPopularMovies(long count) {
        if (count <= 0) {
            throw new ValidationException(String.format("The count:%d of popular movies should be a positive number."
                    , count));
        }
        final List<Film> films = filmStorage.findPopularMovies(count);
        films.forEach(film -> film.setGenres(genreStorage.findGenresForFilm(film.getId())));
        return films;
    }
}