package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
@AutoConfigureTestDatabase
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    Mpa mpa = Mpa.builder().id(1L).name("G").build();
    private final Film film = Film.builder()
            .id(1L)
            .name("RRR")
            .description("Indian")
            .releaseDate(LocalDate.of(2022, 3, 24))
            .duration(187)
            .mpa(mpa)
            .genres(new ArrayList<>())
            .build();

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
    public void addFilmFailNameShouldGiveException() throws Exception {
        film.setName("");

        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"name - Название фильма не может быть пустым\"}"));
    }

    @Test
    public void addFilmFailDescriptionShouldGiveException() throws Exception {
        film.setDescription("Британская Индия. Рама — преданный полицейский на службе у колониального правительства, способный расправиться с толпой в одиночку, " +
                "и местные считают его предателем. Бхим — парень из племени гондов, который вынужден отправиться в Дели на поиски похищенной губернатором маленькой соплеменницы. " +
                "Узнав об этом, губернатор объявляет награду за его поимку, и, чтобы продвинуться по службе, Рама берётся за это задание. " +
                "Спасая упавшего в реку мальчика, Рама и Бхим становятся друзьями. Они не знают ни настоящих личностей, ни скрытых мотивов друг друга, " +
                "но это знакомство положит начало большим переменам.");

        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"description - Максимальное количество символов - 200\"}"));
    }

    @Test
    public void addFilmFailReleaseDateShouldGiveException() throws Exception {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"releaseDate - Фильм должен быть позже 1895-12-28\"}"));
    }

    @Test
    public void addFilmFailDurationShouldGiveException() throws Exception {
        film.setDuration(-100);

        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"duration - Продолжительность фильма должна быть положительной\"}"));
    }

    @Test
    public void addFilmZeroDurationShouldGiveException() throws Exception {
        film.setDuration(0);

        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"duration - Продолжительность фильма должна быть положительной\"}"));
    }

    @Test
    public void updateFilmShouldGiveStatus200andFilmReturned() throws Exception {
        mockMvc.perform(post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON));

        film.setId(1L);
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
    public void updateFailFilmShouldGiveException() throws Exception {
        film.setName("WWW");
        film.setDescription("Super Film");
        mockMvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"error\":\"Фильм c id 1 не существует\"}"));
    }

    @Test
    public void getAllFilmShouldGive2() throws Exception {
        Film filmFirst = Film.builder()
                .id(1L)
                .name("First")
                .description("Comedy film")
                .releaseDate(LocalDate.of(2015, 12, 1))
                .duration(143)
                .mpa(mpa)
                .build();


        Film filmSecond = Film.builder()
                .id(2L)
                .name("Second")
                .description("Action film")
                .releaseDate(LocalDate.of(2020, 3, 28))
                .duration(120)
                .mpa(mpa)
                .build();

        mockMvc.perform(post("/films")
                .content(objectMapper.writeValueAsString(filmFirst))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                .content(objectMapper.writeValueAsString(filmSecond))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/films"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(filmFirst, filmSecond))));

    }

}