package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review create(Review review);

    Review update(Review review);

    void removeReview(Long id);

    Review getById(Long id);

    List<Review> getReviewsByFilmId(Long id, Integer count);

    void addLike(Long id, Long userId);

    void addDislike(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    void deleteDislike(Long id, Long userId);
}
