package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Integer.compare;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
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
        return filmStorage.getCommonFilms(userId, friendId);
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
        Set<Film> foundFilms = new HashSet<>();
        if (parameters.contains("title")) {
            foundFilms.addAll(filmStorage.findFilmsByTitle(findingSubstring));
        }
        if (parameters.contains("director")) {
            foundFilms.addAll(filmStorage.findFilmsByDirector(findingSubstring));
        }
        return foundFilms.stream()
                .sorted((p0, p1) -> {
                    int comp = compare(p0.getLikes().size(), p1.getLikes().size());
                    return -1 * comp;
                })
                .collect(Collectors.toList());
    }
}
