package ru.yandex.practicum.filmorate.storage.genre.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAll() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, this::createGenre);
    }

    @Override
    public Genre getById(Long id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, this::createGenre, id);
        if (genres.size() != 1) {
            throw new EntityNotFoundException(String.format("Жанр с id %s отсутствует", id));
        }
        return genres.get(0);
    }

    @Override
    public List<Genre> getGenreListByFilmId(Long filmId) {
        String sqlQuery = "SELECT * FROM genres WHERE id IN (SELECT genre_id FROM film_genres WHERE film_id = ?)";

        return jdbcTemplate.query(sqlQuery, this::createGenre, filmId);
    }

    @Override
    public void setFilmsGenres(Long filmId, Set<Genre> genres) {
        String sqlQueryCleanFilmsGenres = "DELETE FROM film_genres WHERE film_id = ?";
        String sqlQuerySetGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQueryCleanFilmsGenres, filmId);

        if (genres != null) {
            for (Genre genre : Set.copyOf(genres)) {
                jdbcTemplate.update(sqlQuerySetGenres, filmId, genre.getId());
            }
        }
    }


    private Genre createGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}
