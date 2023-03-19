package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    void remove(Film film);

    Film findById(long id);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> findPopularMovies(long count);
}