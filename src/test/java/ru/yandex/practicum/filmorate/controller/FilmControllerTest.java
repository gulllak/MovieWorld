package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class FilmControllerTest {
    private final Film film = new Film();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        film.setName("RRR");
        film.setDescription("Indian");
        film.setReleaseDate(LocalDate.of(2022, 3, 24));
        film.setDuration(187);
    }

    @Test
    public void addFilmShouldGiveStatus200andFilmReturned() throws Exception {
        mockMvc.perform(post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("RRR"))
                .andExpect(jsonPath("$.description").value("Indian"))
                .andExpect(jsonPath("$.releaseDate").value("2022-03-24"))
                .andExpect(jsonPath("$.duration").value(187));
    }

    @Test
    public void addFilmFailNameShouldGiveException() {
        film.setName("");

        final NestedServletException exception = assertThrows(
                NestedServletException.class,
                () -> mockMvc.perform(post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is5xxServerError()));

        assertEquals("name - Название фильма не может быть пустым", Objects.requireNonNull(exception.getMessage()).substring(108));
    }

    @Test
    public void addFilmFailDescriptionShouldGiveException() {
        film.setDescription("Британская Индия. Рама — преданный полицейский на службе у колониального правительства, способный расправиться с толпой в одиночку, " +
                "и местные считают его предателем. Бхим — парень из племени гондов, который вынужден отправиться в Дели на поиски похищенной губернатором маленькой соплеменницы. " +
                "Узнав об этом, губернатор объявляет награду за его поимку, и, чтобы продвинуться по службе, Рама берётся за это задание. " +
                "Спасая упавшего в реку мальчика, Рама и Бхим становятся друзьями. Они не знают ни настоящих личностей, ни скрытых мотивов друг друга, " +
                "но это знакомство положит начало большим переменам.");

        final NestedServletException exception = assertThrows(
                NestedServletException.class,
                () -> mockMvc.perform(post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is5xxServerError()));

        assertEquals("description - Максимальное количество символов - 200", Objects.requireNonNull(exception.getMessage()).substring(108));
    }

    @Test
    public void addFilmFailReleaseDateShouldGiveException() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        final NestedServletException exception = assertThrows(
                NestedServletException.class,
                () -> mockMvc.perform(post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is5xxServerError()));

        assertEquals("releaseDate - Фильм должен быть позже 1895-12-28", Objects.requireNonNull(exception.getMessage()).substring(108));
    }

    @Test
    public void addFilmFailDurationShouldGiveException() {
        film.setDuration(-100);

        final NestedServletException exception = assertThrows(
                NestedServletException.class,
                () -> mockMvc.perform(post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is5xxServerError()));

        assertEquals("duration - Продолжительность фильма должна быть положительной", Objects.requireNonNull(exception.getMessage()).substring(108));
    }

    @Test
    public void addFilmZeroDurationShouldGiveException() {
        film.setDuration(0);

        final NestedServletException exception = assertThrows(
                NestedServletException.class,
                () -> mockMvc.perform(post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is5xxServerError()));

        assertEquals("duration - Продолжительность фильма должна быть положительной", Objects.requireNonNull(exception.getMessage()).substring(108));
    }

    @Test
    public void updateFilmShouldGiveStatus200andFilmReturned() throws Exception {
        mockMvc.perform(post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON));

        film.setId(1);
        film.setName("WWW");
        film.setDescription("Super Film");
        mockMvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("WWW"))
                .andExpect(jsonPath("$.description").value("Super Film"))
                .andExpect(jsonPath("$.releaseDate").value("2022-03-24"))
                .andExpect(jsonPath("$.duration").value(187));
    }

    @Test
    public void updateFailFilmShouldGiveException() {
        film.setName("WWW");
        film.setDescription("Super Film");
        final NestedServletException exception = assertThrows(
                NestedServletException.class,
                () -> mockMvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError()));

        assertEquals("Фильм не найден", Objects.requireNonNull(exception.getMessage()).substring(108));
    }

    @Test
    public void getAllFilmShouldGive2() throws Exception {
        Film filmFirst = new Film();
        filmFirst.setName("First");
        filmFirst.setDescription("Comedy film");
        filmFirst.setReleaseDate(LocalDate.of(2015, 12, 1));
        filmFirst.setDuration(143);

        Film filmSecond = new Film();
        filmSecond.setName("Second");
        filmSecond.setDescription("Action film");
        filmSecond.setReleaseDate(LocalDate.of(2020, 3, 28));
        filmSecond.setDuration(120);

        mockMvc.perform(post("/films")
                .content(objectMapper.writeValueAsString(filmFirst))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                .content(objectMapper.writeValueAsString(filmSecond))
                .contentType(MediaType.APPLICATION_JSON));

        filmFirst.setId(1);
        filmSecond.setId(2);

        mockMvc.perform(get("/films"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(filmFirst, filmSecond))));

    }

}