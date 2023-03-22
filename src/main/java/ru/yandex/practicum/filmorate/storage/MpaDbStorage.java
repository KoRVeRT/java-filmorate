package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAll() {
        final String getAll = "SELECT * FROM MPA";
        return jdbcTemplate.query(getAll, mpaRowMapperMAP);
    }

    @Override
    public Mpa findById(long id) {
        final String getById = "SELECT * FROM MPA WHERE MPA_ID = ?";
        try {
            return jdbcTemplate.queryForObject(getById, mpaRowMapperMAP, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("MPA rating by id not found.");
        }
    }

    private final RowMapper<Mpa> mpaRowMapperMAP = (rs, rowNum) -> Mpa.builder()
            .id(rs.getInt("MPA_ID"))
            .name(rs.getString("MPA_NAME"))
            .build();
}