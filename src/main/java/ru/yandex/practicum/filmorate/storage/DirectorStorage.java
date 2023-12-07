package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {
    List<Director> findAll();

    Director getById(Long id);

    Director create(Director director);

    Director update(Director director);

    void remove(Long id);

    void setFilmsDirectors(Long filmId, Set<Director> directors);
}
