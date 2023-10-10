package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilmNotFoundException extends RuntimeException{
    public FilmNotFoundException(String message) {
        super(message);
        log.error(message);
    }
}