package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@RequestBody @Valid Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@RequestBody @Valid Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> removeReview(@PathVariable(value = "id") Long id) {
        reviewService.removeReview(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable(value = "id") Long id) {
        return reviewService.getById(id);
    }

    @GetMapping
    public List<Review> getReviewsByFilmId(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false, defaultValue = "10") Integer count) {
        return reviewService.getReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<HttpStatus> addLike(@PathVariable(value = "id") Long id,
                                              @PathVariable(value = "userId") Long userId) {
        reviewService.addLike(id, userId);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<HttpStatus> addDislike(@PathVariable(value = "id") Long id,
                                                 @PathVariable(value = "userId") Long userId) {
        reviewService.addDislike(id, userId);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<HttpStatus> removeLike(@PathVariable(value = "id") Long id,
                                              @PathVariable(value = "userId") Long userId) {
        reviewService.removeLike(id, userId);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<HttpStatus> removeDislike(@PathVariable(value = "id") Long id,
                                                 @PathVariable(value = "userId") Long userId) {
        reviewService.removeDislike(id, userId);

        return ResponseEntity.ok(HttpStatus.OK);
    }
}