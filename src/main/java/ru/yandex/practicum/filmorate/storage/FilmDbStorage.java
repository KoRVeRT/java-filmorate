package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        final String sql = "SELECT * FROM FILMS INNER JOIN MPA ON MPA.MPA_ID=FILMS.MPA_ID";
        return jdbcTemplate.query(sql, this::filmMapRow);
    }

    @Override
    public Film findById(long id) {
        final String sql = "SELECT * FROM FILMS INNER JOIN MPA ON MPA.MPA_ID=FILMS.MPA_ID  where FILMS.FILM_ID = ?";
        return jdbcTemplate.queryForObject(sql, this::filmMapRow, id);
    }

    @Override
    public Film create(Film film) {
        final String sql = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)" +
                "VALUES (?, ?, ?, ?, ?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(psc -> {
            final PreparedStatement ps = psc.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId((Integer) keyHolder.getKey());
        createGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
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
        createGenres(film);
        return film;
    }

    @Override
    public void remove(Film film) {
        final String sql = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());

    }

    @Override
    public List<Film> findPopularMovies(long count) {
        String sql = "SELECT * FROM FILMS AS F INNER JOIN MPA ON MPA.MPA_ID = F.MPA_ID " +
                "LEFT OUTER JOIN LIKES L ON L.FILM_ID = F.FILM_ID " +
                "GROUP BY F.FILM_ID, L.USER_ID " +
                "ORDER BY count(DISTINCT L.USER_ID) DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::filmMapRow, count);
    }

    @Override
    public void addLike(long filmId, long userId) {
        final String sql = "INSERT INTO LIKES(FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);

    }

    @Override
    public void removeLike(long filmId, long userId) {
        final String sql = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private Film filmMapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("FILM_ID"))
                .name((rs.getString("NAME")))
                .releaseDate((rs.getDate("RELEASE_DATE")).toLocalDate())
                .description(rs.getString("DESCRIPTION"))
                .duration(rs.getInt("DURATION"))
                .mpa(Mpa.builder()
                        .id(rs.getInt("MPA_ID"))
                        .name(rs.getString("MPA_NAME"))
                        .build())
                .genres(new ArrayList<>())
                .build();
    }

    public boolean containsFilm(long filmId) {
        final String sql = "SELECT * FROM FILMS INNER JOIN MPA ON MPA.MPA_ID=FILMS.MPA_ID  where FILMS.FILM_ID = ?";
        try {
            jdbcTemplate.queryForObject(sql, this::filmMapRow, filmId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private void createGenres(Film film) {
        final List<Genre> genreList = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate("MERGE INTO FILMS_GENRES key(film_id, genre_id) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, genreList.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                });
    }

    private void removeGenres(Film film) {
        final String sql = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());
    }
}