package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;

    @Override
    public List<Film> findAll() {
        final String sql = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sql, this::filmMapRow);
    }

    @Override
    public Film findById(long id) throws NotFoundException {
        final String sql = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::filmMapRow, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Film id not found");
        }
    }

    @Override
    public Film create(Film film) throws RuntimeException {
        final String sql = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)" +
                "VALUES (?, ?, ?, ?, ?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(psc -> {
            final PreparedStatement ps = psc.prepareStatement(sql, new String[]{"FILM_ID"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            film.setId((Integer) keyHolder.getKey());
            createGenres(film.getId(), film.getGenres());
        } else {
            throw new ValidationException("Movie failed to create id.");
        }
        return findById(film.getId());
    }

    @Override
    public Film update(Film film) throws NotFoundException {
        String sql =
                "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";
        if (jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate().format(DateTimeFormatter.ISO_DATE),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()) <= 0) {
            throw new NotFoundException("Film id not found");
        }
        removeGenres(film);
        createGenres(film.getId(), film.getGenres());
        return findById(film.getId());
    }

    @Override
    public void remove(Film film) throws NotFoundException {
        final String sql = "DELETE FROM FILMS WHERE FILM_ID = ?";
        if (jdbcTemplate.update(sql, film.getId()) <= 0) {
            throw new NotFoundException("Film not deleted");
        }
    }

    @Override
    public List<Film> findPopularMovies(long count) {
        final String sql =
                "SELECT FILMS.* " +
                        "FROM FILMS " +
                        "LEFT JOIN (" +
                        "    SELECT LIKES.FILM_ID," +
                        "           COUNT(LIKES.USER_ID) c" +
                        "    FROM LIKES" +
                        "    GROUP BY FILM_ID" +
                        ") AS LIKES ON LIKES.FILM_ID = FILMS.FILM_ID " +
                        "ORDER BY c DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sql, this::filmMapRow, count);
    }

    @Override
    public void addLike(Film film, User user) throws NotFoundException {
        final String sql = "INSERT INTO LIKES(FILM_ID, USER_ID) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, film.getId(), user.getId());
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Like not added");
        }
    }

    @Override
    public void removeLike(Film film, User user) throws NotFoundException {
        final String sql = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        if (jdbcTemplate.update(sql, film.getId(), user.getId()) <= 0) {
            throw new NotFoundException("Like not deleted");
        }
    }

    private Film filmMapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .mpa(mpaStorage.findById(rs.getInt("MPA_ID")))
                .genres(genreStorage.getFilmGenres(rs.getInt("FILM_ID")))
                .build();
    }

    private void createGenres(long filmId, Collection<Genre> genres) {
        if (genres == null || genres.isEmpty()) return;
        final List<Genre> genresList = new ArrayList<>(genres);
        final String sql =
                "MERGE INTO FILMS_GENRES(FILM_ID, GENRE_ID) VALUES (?, ?) ";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                final Genre genre = genresList.get(i);
                ps.setLong(1, filmId);
                ps.setLong(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genresList.size();
            }
        });
    }

    private void removeGenres(Film film) {
        final String sql = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());
    }
}