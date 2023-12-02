package ru.yandex.practicum.filmorate.storage.director.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> findAll() {
        String sqlQuery = "SELECT * FROM directors";
        return jdbcTemplate.query(sqlQuery, this::createDirector);
    }

    @Override
    public Director getById(Long id) {
        String sqlQuery = "SELECT * FROM directors WHERE id = ?";
        List<Director> directors = jdbcTemplate.query(sqlQuery, this::createDirector, id);
        if (directors.size() != 1) {
            throw new EntityNotFoundException(String.format("Директор с id %s отсутствует", id));
        }
        return directors.get(0);
    }

    @Override
    public Director create(Director director) {
        String sqlQuery = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, director.getName());

            return stmt;
        }, keyHolder);

        return getById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "UPDATE directors SET name = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());

        return getById(director.getId());
    }

    @Override
    public void remove(Long id) {
        String sqlQuery = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void setFilmsDirectors(Long filmId, Set<Director> directors) {
        String sqlQueryCleanFilmsDirectors = "DELETE FROM film_directors WHERE film_id = ?";
        String sqlQuerySetDirectors = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQueryCleanFilmsDirectors, filmId);

        if (directors != null) {
            for (Director director : Set.copyOf(directors)) {
                jdbcTemplate.update(sqlQuerySetDirectors, filmId, director.getId());
            }
        }
    }

    private Director createDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}
