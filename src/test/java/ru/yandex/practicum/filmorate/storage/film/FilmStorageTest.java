package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmStorageTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    User user = User.builder()
            .id(1)
            .email("user@email.ru")
            .name("vanya123")
            .login("Ivan Petrov")
            .birthday(LocalDate.of(1990, 1, 1))
            .build();

    User user1 = User.builder()
            .id(2)
            .email("user1@email.ru")
            .name("Petya")
            .login("Petyyya")
            .birthday(LocalDate.of(1994, 2, 1))
            .build();

    User user2 = User.builder()
            .id(3)
            .email("user2@email.ru")
            .name("Sasha Petrov")
            .login("Sasha")
            .birthday(LocalDate.of(1970, 1, 1))
            .build();

    Mpa mpa1 = Mpa.builder().id(4).build();
    Mpa mpa1Full = Mpa.builder().id(4).name("R").build();
    Mpa mpa2 = Mpa.builder().id(3).build();
    Mpa mpa2Full = Mpa.builder().id(3).name("PG-13").build();



    Genre genre1 = Genre.builder().id(2).build();
    Genre genre1Full = Genre.builder().id(2).name("Драма").build();
    Genre genre2 = Genre.builder().id(1).build();
    Genre genre2Full = Genre.builder().id(1).name("Комедия").build();


    Film film = Film.builder()
            .id(1)
            .name("Опенгеймер")
            .description("История жизни американского физика-теоретика Роберта Оппенгеймера, " +
                    "который во времена Второй мировой войны руководил Манхэттенским проектом — " +
                    "секретными разработками ядерного оружия.")
            .releaseDate(LocalDate.of(2023, 7, 19))
            .duration(180)
            .mpa(mpa1)
            .genres(List.of(genre1))
            .build();

    Film film1 = Film.builder()
            .id(2)
            .name("Форрест Гамп")
            .description("С самого малолетства парень страдал от заболевания ног, соседские мальчишки дразнили его, " +
                    "но в один прекрасный день Форрест открыл в себе невероятные способности к бегу. ")
            .releaseDate(LocalDate.of(1994, 6, 23))
            .duration(142)
            .mpa(mpa2)
            .genres(List.of(genre2))
            .build();

    @Test
    public void createFilm() {
        filmStorage.create(film);
        film.setMpa(mpa1Full);
        film.setGenres(List.of(genre1Full));
        Film getFilm = filmStorage.getFilmById(1);

        assertThat(getFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void createFilmWithSeveralGenres() {
        film.setGenres(List.of(genre1, genre2));
        filmStorage.create(film);

        film.setMpa(mpa1Full);
        film.setGenres(List.of(genre2Full, genre1Full));
        Film getFilm = filmStorage.getFilmById(1);

        assertThat(getFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void findAll() {
        filmStorage.create(film);
        filmStorage.create(film1);

        List<Film> getFilms = filmStorage.findAll();

        film.setMpa(mpa1Full);
        film1.setMpa(mpa2Full);
        film1.setGenres(List.of(genre2Full));
        film.setGenres(List.of(genre1Full));

        Assertions.assertEquals(List.of(film, film1), getFilms);
    }

    @Test
    public void getFilmById() {
        filmStorage.create(film);
        film.setMpa(mpa1Full);
        film.setGenres(List.of(genre1Full));

        Film getFilm = filmStorage.getFilmById(1);
        Assertions.assertEquals(film, getFilm);
    }

    @Test
    public void getFilmByInvalidId() {
        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(
                EntityNotFoundException.class, () -> filmStorage.getFilmById(1));
        Assertions.assertEquals("Фильм c id 1 не существует", entityNotFoundException.getMessage());
    }

    @Test
    public void update() {
        filmStorage.create(film);

        film.setName("Обновленный");

        filmStorage.update(film);
        Film getFilm = filmStorage.getFilmById(1);
        film.setMpa(mpa1Full);
        film.setGenres(List.of(genre1Full));

        Assertions.assertEquals(film, getFilm);
    }

    @Test
    public void addLike() {
        filmStorage.create(film);
        userStorage.create(user);
        filmStorage.addLike(1, 1);

        EntityAlreadyExistException entityAlreadyExistException = Assertions.assertThrows(
                EntityAlreadyExistException.class, () -> filmStorage.addLike(1, 1));
        Assertions.assertEquals("Этот пользователь уже ставил лайк", entityAlreadyExistException.getMessage());
    }

    @Test
    public void addLikeForUnknownFilm() {
        userStorage.create(user);

        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(
                EntityNotFoundException.class, () -> filmStorage.addLike(1, 1));
        Assertions.assertEquals("Фильм c id 1 не существует", entityNotFoundException.getMessage());
    }

    @Test
    public void removeLike() {
        filmStorage.create(film);
        userStorage.create(user);
        filmStorage.addLike(1, 1);

        EntityAlreadyExistException entityAlreadyExistException = Assertions.assertThrows(
                EntityAlreadyExistException.class, () -> filmStorage.addLike(1, 1));
        Assertions.assertEquals("Этот пользователь уже ставил лайк", entityAlreadyExistException.getMessage());

        filmStorage.removeLike(1, 1);

        entityAlreadyExistException = Assertions.assertThrows(
                EntityAlreadyExistException.class, () -> filmStorage.removeLike(1, 1));
        Assertions.assertEquals("Этот пользователь не ставил лайк", entityAlreadyExistException.getMessage());
    }

    @Test
    public void getPopularFilm() {
        filmStorage.create(film);
        filmStorage.create(film1);
        userStorage.create(user);
        userStorage.create(user1);
        userStorage.create(user2);

        filmStorage.addLike(1, 1);
        filmStorage.addLike(2, 1);
        filmStorage.addLike(1, 2);
        filmStorage.addLike(1, 3);

        film.setMpa(mpa1Full);
        film1.setMpa(mpa2Full);

        film.setGenres(List.of(genre1Full));
        film1.setGenres(List.of(genre2Full));

        List<Film> getPopularFilm = filmStorage.getPopularFilms(2);

        Assertions.assertEquals(List.of(film, film1), getPopularFilm);
    }


}