package ru.yandex.practicum.filmorate.storage.like.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long filmId, Long userId) {
        if (likeExists(filmId, userId)) {
            throw new EntityAlreadyExistException("Этот пользователь уже ставил лайк");
        }
        String sqlQuery = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        if (!likeExists(filmId, userId)) {
            throw new EntityAlreadyExistException("Этот пользователь не ставил лайк");
        }
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Long> getPopularFilms(Integer limit, Long genreId, Integer year) {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery
                .append("SELECT f.id, COUNT(l.user_id) AS count_likes FROM films AS f ")
                .append("LEFT JOIN likes AS l ON l.film_id = f.id ");
        if (genreId != null && year != null) {
            sqlQuery
                    .append("RIGHT JOIN film_genres AS fg ON fg.film_id = f.id ")
                    .append("WHERE fg.genre_id = ")
                    .append(genreId)
                    .append("AND EXTRACT(YEAR FROM f.releaseDate) = ")
                    .append(year);
        } else if (genreId != null) {
            sqlQuery
                    .append("RIGHT JOIN film_genres AS fg ON fg.film_id = f.id ")
                    .append("WHERE fg.genre_id = ")
                    .append(genreId);
        } else if (year != null) {
            sqlQuery
                    .append("WHERE EXTRACT(YEAR FROM f.releaseDate) = ")
                    .append(year);
        }

        sqlQuery
                .append("GROUP BY f.id ORDER BY count_likes DESC LIMIT ")
                .append(limit);

        return jdbcTemplate.query(sqlQuery.toString(), this::getId);
    }

    @Override
    public List<Long> getCommonFilmIds(Long userId, Long friendId) {
        String sqlQuery = "SELECT l1.film_id\n" +
                "FROM likes AS l1\n" +
                "INNER JOIN likes AS l2 ON l1.film_id = l2.film_id AND l1.user_id <> l2.user_id\n" +
                "WHERE l1.user_id = ? AND l2.user_id = ?;";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId, friendId);
    }

    private boolean likeExists(Long filmId, Long userId) {
        String sqlQuery = "SELECT * FROM likes WHERE user_id = ? AND film_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, filmId);
        return sqlRowSet.next();
    }

    private Long getId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("id");
    }
}
