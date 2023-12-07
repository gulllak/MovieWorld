package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.storage.ReviewLikesStorage;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReviewLikesDbStorage implements ReviewLikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addLike(Long id, Long userId) {
        boolean isChangeLike = false;
        Map<Long, Boolean> afterCheck = checkData(id, userId);
        if (afterCheck.isEmpty()) {
            String sqlQuery = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, id, userId, true);
        } else if (afterCheck.get(userId)) {
            throw new EntityAlreadyExistException("Пользователь уже оценил этот отзыв.");
        } else if (!afterCheck.get(userId)) {
            removeDislike(id, userId);
            addLike(id, userId);
            isChangeLike = true;
        }
        return isChangeLike;
    }

    @Override
    public void removeLike(Long id, Long userId) {
        Map<Long, Boolean> afterCheck = checkData(id, userId);
        if (afterCheck.get(userId).equals(true)) {
            String sqlQuery = "DELETE FROM review_likes WHERE user_id = ? AND is_like = ?";
            jdbcTemplate.update(sqlQuery, userId, true);
        } else if (afterCheck.isEmpty()) {
            throw new EntityAlreadyExistException("Пользователь еще не поставил оценку отзыву.");
        }
    }

    @Override
    public boolean addDislike(Long id, Long userId) {
        boolean isChangeLike = false;
        Map<Long, Boolean> afterCheck = checkData(id, userId);
        if (afterCheck.isEmpty()) {
            String sqlQuery = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, id, userId, false);
        } else if (!afterCheck.get(userId)) {
            throw new EntityAlreadyExistException("Пользователь уже оценил этот отзыв.");
        } else if (afterCheck.get(userId)) {
            removeLike(id, userId);
            addDislike(id, userId);
            isChangeLike = true;
        }
        return isChangeLike;
    }

    @Override
    public void removeDislike(Long id, Long userId) {
        Map<Long, Boolean> afterCheck = checkData(id, userId);
        if (afterCheck.get(userId).equals(false)) {
            String sqlQuery = "DELETE FROM review_likes WHERE user_id = ? AND is_like = ?";
            jdbcTemplate.update(sqlQuery, userId, false);
        } else if (afterCheck.isEmpty()) {
            throw new EntityAlreadyExistException("Пользователь еще не поставил оценку отзыву.");
        }
    }

    private Map<Long, Boolean> checkData(Long id, Long userId) {
        String sqlQuery = "SELECT is_like FROM review_likes WHERE user_id = ? AND review_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, id);
        Map<Long, Boolean> result = new HashMap<>();
        if (sqlRowSet.next()) {
            result.put(userId, sqlRowSet.getBoolean("is_like"));
        }
        return result;
    }
}
