package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    private final LikeStorage likeStorage;

    @Override
    public List<Film> findAll() {
        String sqlQuery = "SELECT * FROM films";

        return jdbcTemplate.query(sqlQuery, this::createFilm);
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, releasedate, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        int filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        genreStorage.setFilmsGenres(filmId, film.getGenres());
        return getFilmById(filmId);
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, releasedate = ?, duration = ?, mpa_id =? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        genreStorage.setFilmsGenres(film.getId(), film.getGenres());
        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::createFilm, id);
        if (films.size() != 1) {
            throw new EntityNotFoundException(String.format("Фильм c id %s не существует", id));
        }
        return films.get(0);
    }

    @Override
    public void addLike(int filmId, int userId) {
        getFilmById(filmId);

        likeStorage.addLike(filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        likeStorage.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        List<Film> films = new ArrayList<>();

        for (int id : likeStorage.getPopularFilm(count)) {
            films.add(getFilmById(id));
        }
        return films;
    }

    private Film createFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("releasedate").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpaStorage.getById(rs.getInt("mpa_id")))
                .genres(genreStorage.getGenreListByFilmId(rs.getInt("id")))
                .build();
    }
}
