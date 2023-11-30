package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
class UserControllerTest {
    private final User user = User.builder()
            .id(1L)
            .email("mail@mail.ru")
            .login("user")
            .name("Vasya")
            .birthday(LocalDate.of(2000, 5, 5))
            .build();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void addUserShouldGiveStatus200andUserReturned() throws Exception {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.name").value("Vasya"))
                .andExpect(jsonPath("$.birthday").value("2000-05-05"));

    }

    @Test
    public void addUserFailLoginShouldGiveException() throws Exception {
        user.setLogin("user user");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"login - В логине не может быть пробелов\"}"));
    }

    @Test
    public void addUserFailEmailShouldGiveException() throws Exception {
        user.setEmail("mail.ru");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"email - Почта не валидна\"}"));
    }

    @Test
    public void addUserEmptyEmailShouldGiveException() throws Exception {
        user.setEmail("");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"email - Почта не валидна\"}"));
    }

    @Test
    public void addUserFailBirthdayShouldGiveException() throws Exception {
        user.setBirthday(LocalDate.of(2025, 12, 23));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"birthday - Дата рождения не может быть в будущем\"}"));
    }

    @Test
    public void addUserWithEmptyNameShouldGiveStatus200andUserReturned() throws Exception {
        user.setName("");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.birthday").value("2000-05-05"));
    }

    @Test
    public void updateUserShouldGiveStatus200andFilmReturned() throws Exception {
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));

        user.setId(1L);
        user.setName("UUUSSSER");
        user.setLogin("VASKA");

        mockMvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(jsonPath("$.login").value("VASKA"))
                .andExpect(jsonPath("$.name").value("UUUSSSER"))
                .andExpect(jsonPath("$.birthday").value("2000-05-05"));
    }

    @Test
    public void updateFailUserShouldGiveException() throws Exception {
        mockMvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"error\":\"Пользователя c id 1 отсутствует\"}"));
    }

    @Test
    public void getAllUserShouldGive2() throws Exception {
        User userSecond = User.builder()
                .id(2L)
                .email("asd@mail.ru")
                .login("second")
                .name("second")
                .birthday(LocalDate.of(2005, 1, 2))
                .build();

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userSecond))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        user.setId(1L);
        userSecond.setId(2L);

        mockMvc.perform(get("/users"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user, userSecond))));
    }
}