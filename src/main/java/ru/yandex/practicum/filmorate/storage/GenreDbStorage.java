package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Slf4j
@Repository
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAll() {
        final String getAll = "SELECT * FROM GENRES";
        return jdbcTemplate.query(getAll, this::genreRowMapper);
    }

    @Override
    public Genre findById(long id) {
        final String getById = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        try {
            return jdbcTemplate.queryForObject(getById, this::genreRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No genre by id found");
        }
    }

    @Override
    public void getFilmGenres(List<Film> films) {
        String sql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        final String sqlQuery = "select * from GENRES g, FILMS_GENRES fg " +
                "where fg.GENRE_ID = g.GENRE_ID AND fg.FILM_ID IN(" + sql + ")";

        jdbcTemplate.query(sqlQuery, rs -> {
            final Film film = filmById.get(rs.getLong("FILM_ID"));
            film.getGenres().add(genreRowMapper(rs, 0));
        }, films.stream().map(Film::getId).toArray());
    }

    @Override
    public void findGenresForFilm(Film film) {
        String sqlQuery = "SELECT * from GENRES where GENRE_ID in (select GENRE_ID from FILMS_GENRES where FILM_ID = ?)";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, this::genreRowMapper, film.getId());
        film.setGenres(genres);
    }


    private Genre genreRowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }
}
