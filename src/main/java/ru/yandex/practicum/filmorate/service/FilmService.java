package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final LikeStorage likeStorage;

    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage,
                       LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count, Long genreId, Integer year) {
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);

        List<Film> commonFilms = new ArrayList<>();
        List<Long> sdfds = likeStorage.getCommonFilmIds(userId, friendId);
        for (Long id : sdfds) {
            commonFilms.add(filmStorage.getFilmById(id));
        }
        return commonFilms;
    }

    public List<Film> getDirectorFilmsSorted(Long directorId, String sortType) {
        List<Film> films = new ArrayList<>();
        if (sortType.equals("year")) {
            films = filmStorage.getDirectorFilmsByYear(directorId);
        }

        if (sortType.equals("likes")) {
            films = filmStorage.getDirectorFilmsByLikes(directorId);
        }
        return films;
    }

    public void remove(Long filmId) {
        filmStorage.remove(filmId);
    }

    public List<Film> findFilms(String findingSubstring, List<String> parameters) {
        return filmStorage.findFilm(findingSubstring, parameters);
    }
}
