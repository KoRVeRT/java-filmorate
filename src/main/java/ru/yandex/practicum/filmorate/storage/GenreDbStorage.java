package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
    public List<Genre> findGenresForFilm(long filmId) {
        String sqlQuery = "SELECT * from GENRES JOIN FILMS_GENRES ON GENRES.GENRE_ID = FILMS_GENRES.GENRE_ID " +
                "where FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::genreRowMapper, filmId);
    }

    private Genre genreRowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }
}