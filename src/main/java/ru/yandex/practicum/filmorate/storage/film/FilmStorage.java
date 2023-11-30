package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Film getFilmById(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getRecommendations(Long id);

    List<Film> getDirectorFilmsByYear(Long directorId);

    List<Film> getDirectorFilmsByLikes(Long directorId);
}
