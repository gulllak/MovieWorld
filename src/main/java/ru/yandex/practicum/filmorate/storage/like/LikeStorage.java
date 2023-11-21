package ru.yandex.practicum.filmorate.storage.like;

import java.util.List;

public interface LikeStorage {
    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Long> getPopularFilms(int count);
}
