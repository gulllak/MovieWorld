package ru.yandex.practicum.filmorate.storage.reviewLikes;

import java.util.HashMap;

public interface ReviewLikesStorage {
    void addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    void addDislike(Long id, Long userId);

    void deleteDislike(Long id, Long userId);

    HashMap<Boolean, Boolean> checkData(Long reviewId, Long userId);
}
