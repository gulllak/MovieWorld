package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> findAll();

    Director getById(Long id);

    Director create(Director director);

    Director update(Director director);

    void remove(Long id);

    void setFilmsDirectors(Long filmId, List<Director> directors);
}
