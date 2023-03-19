package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final MpaStorage mpaStorage;
    private final UserStorage userStorage;

    @Test
    void listFilms() {
        assertEquals(4, filmDbStorage.findAll().size());
    }

    @Test
    void getFilmById() {
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.findById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void createFilm() {
        Film film = new Film();
        film.setName("film5");
        film.setDescription("description5");
        film.setReleaseDate(LocalDate.ofEpochDay(1991 - 10 - 5));
        film.setDuration(100);

        film.setMpa(mpaStorage.findById(1));
        filmDbStorage.create(film);
        List<Film> films = filmDbStorage.findAll();
        assertEquals(5, films.size());
    }

    @Test
    void updateFilm() {
        Film film = new Film();
        film.setId(1);
        film.setName("FilmUpdated");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.ofEpochDay(1986 - 10 - 10));
        film.setDuration(70);
        film.setMpa(mpaStorage.findById(1));
        filmDbStorage.update(film);
        String value = filmDbStorage.findById(1).getName();
        assertEquals("FilmUpdated", value);
    }

    @Test
    void addLikePlusGetMostPopularFilms() {
        Film film = filmDbStorage.findById(1);
        User user = userStorage.findById(1);
        filmDbStorage.addLike(film, user);
        Film popularFilm = filmDbStorage.findPopularMovies(1).get(0);
        assertEquals(filmDbStorage.findById(1), popularFilm);
    }

    @Test
    void deleteLike() {
        Film film1 = filmDbStorage.findById(1);
        Film film2 = filmDbStorage.findById(2);
        User user1 = userStorage.findById(1);
        User user2 = userStorage.findById(2);
        User user3 = userStorage.findById(3);
        filmDbStorage.addLike(film1, user1);
        filmDbStorage.addLike(film1, user2);
        filmDbStorage.addLike(film2, user1);
        filmDbStorage.addLike(film2, user2);
        filmDbStorage.addLike(film2, user3);
        filmDbStorage.removeLike(film2, user1);
        filmDbStorage.removeLike(film2, user3);
        Film popularFilm = filmDbStorage.findPopularMovies(2).get(1);
        assertEquals(filmDbStorage.findById(2), popularFilm);
    }
}
