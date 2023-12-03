package ru.yandex.practicum.filmorate.storage.reviewLikes;

public interface ReviewLikesStorage {
    boolean addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    boolean addDislike(Long id, Long userId);

    void removeDislike(Long id, Long userId);

}
