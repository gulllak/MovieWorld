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
    public List<Long> getPopularFilms(int count) {
        String sqlQuery = "SELECT f.ID, COUNT(l.user_id) AS col " +
                "FROM films AS f LEFT JOIN likes AS l ON f.id = l.film_id GROUP BY f.id ORDER BY col DESC LIMIT ?";

        return jdbcTemplate.query(sqlQuery,this::getId, count);
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
