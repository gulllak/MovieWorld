package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film updatedFilm) {
        return filmService.update(updatedFilm);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(value = "id") int filmId) {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("{id}/like/{userId}")
    public ResponseEntity<HttpStatus> addLike(@PathVariable(value = "id") int filmId,
                                               @PathVariable(value = "userId") int userId) {
        filmService.addLike(filmId, userId);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<HttpStatus> removeLike(@PathVariable(value = "id") int filmId,
                                                 @PathVariable(value = "userId") int userId) {
        filmService.removeLike(filmId, userId);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilm(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getPopularFilm(count);
    }
}
