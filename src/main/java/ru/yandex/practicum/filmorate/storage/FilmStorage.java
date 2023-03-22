package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    void remove(long filmId);

    Film findById(long id);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    boolean containsFilm(long filmId);

    List<Film> findPopularMovies(long count);
}