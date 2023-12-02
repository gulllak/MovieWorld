package ru.yandex.practicum.filmorate.storage.reviewLikes.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.storage.reviewLikes.ReviewLikesStorage;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReviewLikesDbStorage implements ReviewLikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long id, Long userId) {
        Map<Boolean, Boolean> afterCheck = checkData(id, userId);
        if (afterCheck.containsKey(false)) {
            String sqlQuery = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,id, userId, true);
        } else if (afterCheck.get(true).equals(true)) {
            throw new EntityAlreadyExistException("Пользователь уже поставил лайк отзыву.");
        } else {
            throw new EntityAlreadyExistException("Пользователь уже поставил дизлайк отзыву.");
        }
    }

    @Override
    public void removeLike(Long id, Long userId) {
        Map<Boolean, Boolean> afterCheck = checkData(id, userId);
        if (afterCheck.containsKey(true) && afterCheck.get(true).equals(true)) {
            String sqlQuery = "DELETE FROM review_likes WHERE user_id = ? AND is_like = ?";
            jdbcTemplate.update(sqlQuery, userId, true);
        } else if (afterCheck.get(false)) {
            throw new EntityAlreadyExistException("Пользователь еще не поставил оценку отзыву.");
        }
    }

    @Override
    public void addDislike(Long id, Long userId) {
        Map<Boolean, Boolean> afterCheck = checkData(id, userId);
        if (afterCheck.containsKey(false)) {
            String sqlQuery = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, id, userId, false);
        } else if (afterCheck.get(true).equals(true)) {
            throw new EntityAlreadyExistException("Пользователь уже поставил лайк отзыву.");
        } else {
            throw new EntityAlreadyExistException("Пользователь уже поставил дизлайк отзыву.");
        }
    }

    @Override
    public void deleteDislike(Long id, Long userId) {
        Map<Boolean, Boolean> afterCheck = checkData(id, userId);
        if (afterCheck.containsKey(true) && afterCheck.get(true).equals(false)) {
            String sqlQuery = "DELETE FROM review_likes WHERE user_id = ? AND is_like = ?";
            jdbcTemplate.update(sqlQuery, userId, false);
        } else if (afterCheck.get(false)) {
            throw new EntityAlreadyExistException("Пользователь еще не поставил оценку отзыву.");
        }
    }

    @Override
    public HashMap<Boolean, Boolean> checkData(Long reviewId, Long userId) {
        String sqlQuery = "SELECT is_like FROM review_likes WHERE review_id = ? AND user_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, reviewId,  userId);
        Map<Boolean, Boolean> check = new HashMap<>();
        if (sqlRowSet.next() == false) {
            check.put(false, null);
        } else {
            check.put(true, sqlRowSet.getBoolean("is_like"));
        }
        return (HashMap<Boolean, Boolean>) check;
    }
}
