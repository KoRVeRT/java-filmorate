package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface CommonFilmService {
    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Film findById(long id);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

    List<Film> findPopularMovies(long count);
}