package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface LikeStorage {
    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Long> getPopularFilms(Integer limit, Long genreId, Integer year);

    List<Long> getCommonFilmIds(Long userId, Long friendId);

    List<Long> getLikedFilmsByUserId(Long id);
}
