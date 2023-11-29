package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private Long id = 0L;

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        film.setLikes(new HashSet<>());

        films.put(film.getId(), film);
        log.info("Добавлен новый фильм");
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            film.setLikes(films.get(film.getId()).getLikes());

            films.put(film.getId(), film);
            log.info("Данные фильма изменены");
        } else {
            throw new EntityNotFoundException("Фильм не найден");
        }
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        if (!films.containsKey(id)) {
            throw new EntityNotFoundException("Фильм не найден");
        }
        return films.get(id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        films.get(filmId).getLikes().add(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        films.get(filmId).getLikes().remove(userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return findAll().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return null;
    }

    @Override
    public List<Film> getDirectorFilmsByYear(Long directorId) {
        return null;
    }

    @Override
    public List<Film> getDirectorFilmsByLikes(Long directorId) {
        return null;
    }

    private Long getNextId() {
        return ++id;
    }
}
