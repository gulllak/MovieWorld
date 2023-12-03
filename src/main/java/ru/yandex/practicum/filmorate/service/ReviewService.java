package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review create(Review review) {
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        return reviewStorage.update(review);
    }

    public void removeReview(Long id) {
        reviewStorage.removeReview(id);
    }

    public Review getById(Long id) {
        return reviewStorage.getById(id);
    }

    public List<Review> getReviewsByFilmId(Long id, Integer count) {
        return reviewStorage.getReviewsByFilmId(id, count);
    }

    public void addLike(Long id, Long userId) {
        reviewStorage.addLike(id, userId);
    }

    public void addDislike(Long id, Long userId) {
        reviewStorage.addDislike(id, userId);
    }

    public void removeLike(Long id, Long userId) {
        reviewStorage.removeLike(id, userId);
    }

    public void removeDislike(Long id, Long userId) {
        reviewStorage.removeDislike(id, userId);
    }
}
