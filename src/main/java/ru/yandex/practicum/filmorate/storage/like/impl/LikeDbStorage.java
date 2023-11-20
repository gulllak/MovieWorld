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
    public void addLike(int filmId, int userId) {
        if (likeExists(filmId, userId)) {
            throw new EntityAlreadyExistException("Этот пользователь уже ставил лайк");
        }
        String sqlQuery = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        if (!likeExists(filmId, userId)) {
            throw new EntityAlreadyExistException("Этот пользователь не ставил лайк");
        }
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Integer> getPopularFilm(int count) {
        String sqlQuery = "SELECT f.ID, COUNT(l.user_id) AS col " +
                "FROM films AS f LEFT JOIN likes AS l ON f.id = l.film_id GROUP BY f.id ORDER BY col DESC LIMIT ?";

        return jdbcTemplate.query(sqlQuery,this::getId, count);
    }

    private boolean likeExists(int filmId, int userId) {
        String sqlQuery = "SELECT * FROM likes WHERE user_id = ? AND film_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, filmId);
        return sqlRowSet.next();
    }

    private Integer getId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("id");
    }
}
