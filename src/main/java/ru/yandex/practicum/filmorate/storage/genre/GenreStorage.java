package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> findAll();

    Genre getById(int id);

    List<Genre> getGenreListByFilmId(int filmId);

    void setFilmsGenres(int filmId, List<Genre> genres);
}
