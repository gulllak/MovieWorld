package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private final GenreStorage genreStorage;

    private final LikeStorage likeStorage;

    @Override
    public List<Film> findAll() {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, m.id AS mpa_id, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.id " +
                "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, m.id, m.name, g.id, g.name";
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
        String sqlQuery = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, m.id AS mpa_id, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.id " +
                "WHERE f.id = ? " +
                "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, m.id, m.name, g.id, g.name";

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
    public List<Film> getPopularFilms(int count) {
        List<Film> films = new ArrayList<>();

        for (int id : likeStorage.getPopularFilms(count)) {
            films.add(getFilmById(id));
        }
        return films;
    }

    private List<Film> createFilm(ResultSet rs) throws SQLException {
        ResultSetExtractor<List<Film>> resultSetExtractor = rs1 -> {
            Map<Integer, Film> list = new HashMap<>();
            while (rs1.next()) {
                if (list.containsKey(rs1.getInt("id"))) {
                    list.get(rs1.getInt("id")).getGenres().add(Genre.builder()
                            .id(rs1.getInt("genre_id"))
                            .name(rs1.getString("genre_name"))
                            .build());
                } else {
                    Film film = Film.builder()
                            .id(rs1.getInt("id"))
                            .name(rs1.getString("name"))
                            .description(rs1.getString("description"))
                            .releaseDate(rs1.getDate("releasedate").toLocalDate())
                            .duration(rs1.getInt("duration"))
                            .mpa(Mpa.builder()
                                    .id(rs1.getInt("mpa_id"))
                                    .name(rs1.getString("mpa_name"))
                                    .build())
                            .genres(new ArrayList<>())
                            .build();

                    if (rs1.getInt("genre_id") != 0) {
                        film.getGenres().add(Genre.builder()
                                .id(rs1.getInt("genre_id"))
                                .name(rs1.getString("genre_name"))
                                .build());
                    }

                    list.put(film.getId(), film);
                }
            }
            return new ArrayList<>(list.values());
        };
        return resultSetExtractor.extractData(rs);
    }
}
