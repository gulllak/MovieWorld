package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorStorage directorStorage;

    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getAll() {
        return directorStorage.findAll();
    }

    public Director getById(Long id) {
        return directorStorage.getById(id);
    }

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        directorStorage.getById(director.getId());

        return directorStorage.update(director);
    }

    public void remove(Long id) {
        directorStorage.getById(id);

        directorStorage.remove(id);
    }
}
