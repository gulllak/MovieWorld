package ru.yandex.practicum.filmorate.storage.review.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.reviewLikes.ReviewLikesStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final ReviewLikesStorage reviewLikesStorage;

    @Override
    public Review create(Review review) {
        checkDataExist(review.getUserId(), review.getFilmId());
        String sqlQuery = "INSERT INTO reviews (is_positive, content, user_id, film_id, useful) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setBoolean(1, review.getIsPositive());
            stmt.setString(2, review.getContent());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            stmt.setInt(5, review.getUseful());
            return stmt;
        }, keyHolder);
        return getById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public Review update(Review review) {
        checkDataExist(review.getUserId(), review.getFilmId());
        String sqlQuery = "UPDATE reviews SET is_positive = ?, content = ? WHERE id =?";
        jdbcTemplate.update(sqlQuery, review.getIsPositive(), review.getContent(), review.getReviewId());
        return getById(review.getReviewId());
    }

    @Override
    public void removeReview(Long id) {
        jdbcTemplate.update("DELETE FROM reviews WHERE id = ?", id);
    }

    @Override
    public Review getById(Long id) {
        String sqlQuery = "SELECT * FROM reviews WHERE id = ?";
        List<Review> reviews = jdbcTemplate.query(sqlQuery, this::createReview, id);
        if (reviews.size() != 1) {
            throw new EntityNotFoundException(String.format("Отзыв с id %s отсутствует.", id));
        }
        return reviews.get(0);
    }

    @Override
    public List<Review> getReviewsByFilmId(Long id, Integer count) {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT * FROM reviews ");
        List<Review> reviews;
        if (id != null) {
            sqlQuery.append("WHERE film_id = ? ")
                    .append("ORDER BY useful DESC ")
                    .append("LIMIT ?");

            reviews = jdbcTemplate.query(sqlQuery.toString(), this::createReview, id, count);
        } else {
            sqlQuery.append("ORDER BY useful DESC ")
                    .append("LIMIT ?");

            reviews = jdbcTemplate.query(sqlQuery.toString(), this::createReview, count);
        }
        return reviews;
    }

    @Override
    public void addLike(Long id, Long userId) {
        userStorage.getUserById(userId);
        boolean isChangeLike = reviewLikesStorage.addLike(id, userId);
        String sqlQuery;
        int useful;
        if (!isChangeLike) {
            useful = getById(id).getUseful() + 1;
        } else {
            useful = getById(id).getUseful() + 2;
        }
        sqlQuery = "UPDATE reviews SET useful = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, useful, id);
    }

    @Override
    public void addDislike(Long id, Long userId) {
        userStorage.getUserById(userId);
        boolean isChangeLike = reviewLikesStorage.addDislike(id, userId);
        String sqlQuery;
        int useful;
        if (!isChangeLike) {
            useful = getById(id).getUseful() - 1;
        } else {
            useful = getById(id).getUseful() - 2;
        }
        sqlQuery = "UPDATE reviews SET useful = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, useful, id);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        userStorage.getUserById(userId);
        reviewLikesStorage.removeLike(id, userId);
        Integer useful = getById(id).getUseful() - 1;
        String sqlQuery = "UPDATE reviews SET useful = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, useful, id);
    }

    @Override
    public void removeDislike(Long id, Long userId) {
        userStorage.getUserById(userId);
        reviewLikesStorage.removeDislike(id, userId);
        Integer useful = getById(id).getUseful() + 1;
        String sqlQuery = "UPDATE reviews SET useful = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, useful, id);
    }

    private void checkDataExist(Long userId, Long filmId) {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
    }

    private Review createReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("id"))
                .isPositive(rs.getBoolean("is_positive"))
                .content(rs.getString("content"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }
}
