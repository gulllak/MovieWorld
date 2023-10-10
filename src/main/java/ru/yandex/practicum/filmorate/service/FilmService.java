package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public void addLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);

        film.getLikes().add(userId);
        film.setRate(film.getRate() + 1);

    }

    public void removeLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);

        film.getLikes().remove(userId);
        film.setRate(film.getRate() - 1);
    }

    public List<Film> getPopularFilm(int count) {
        List<Film> allFilms = filmStorage.findAll();

        return allFilms.stream()
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
