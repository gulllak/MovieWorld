package ru.yandex.practicum.filmorate.storage.like.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    private final EventStorage eventStorage;

    @Override
    public void addLike(Long filmId, Long userId) {
        if (likeExists(filmId, userId)) {
            eventStorage.addEvent(userId, filmId, EventType.LIKE, Operation.ADD);
        } else {
            String sqlQuery = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, userId, filmId);
            eventStorage.addEvent(userId, filmId, EventType.LIKE, Operation.ADD);
        }
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        if (!likeExists(filmId, userId)) {
            throw new EntityAlreadyExistException("Этот пользователь не ставил лайк");
        }
        jdbcTemplate.update(sqlQuery, userId, filmId);
        eventStorage.addEvent(userId, filmId, EventType.LIKE, Operation.REMOVE);

    }

    @Override
    public List<Long> getPopularFilms(Integer limit, Long genreId, Integer year) {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery
                .append("SELECT f.id AS film_id, COUNT(l.user_id) AS count_likes FROM films AS f ")
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
        String sqlQuery = "SELECT shared_likes.film_id, all_likes.total_like_count " +
                "FROM (SELECT film_id " +
                "    FROM likes " +
                "    WHERE user_id = ? " +
                "    INTERSECT " +
                "    SELECT film_id " +
                "    FROM likes " +
                "    WHERE user_id = ?) AS shared_likes " +
                "JOIN (SELECT film_id, COUNT(*) AS total_like_count " +
                "    FROM likes " +
                "    GROUP BY film_id) AS all_likes ON shared_likes.film_id = all_likes.film_id " +
                "ORDER BY all_likes.total_like_count DESC";
        return jdbcTemplate.query(sqlQuery, this::getId, userId, friendId);
    }

    @Override
    public List<Long> getLikedFilmsByUserId(Long id) {
        String sqlQuery = "SELECT film_id FROM likes WHERE user_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, id);
    }

    private boolean likeExists(Long filmId, Long userId) {
        String sqlQuery = "SELECT * FROM likes WHERE user_id = ? AND film_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, filmId);
        return sqlRowSet.next();
    }

    private Long getId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("film_id");
    }
}
