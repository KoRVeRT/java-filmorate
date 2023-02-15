package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Film findById(long id);

    void addLike(long filmIds, long userIds);

    void deleteLike(long filmIds, long userIds);

    List<Film> findPopularMovies(long count);
}