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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
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
    private final User user = new User();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        UserController.id = 0;

        user.setEmail("mail@mail.ru");
        user.setLogin("user");
        user.setName("Vasya");
        user.setBirthday(LocalDate.of(2000, 5, 5));
    }

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
    public void addUserFailLoginShouldGiveException() {
        user.setLogin("user user");

        final NestedServletException exception = assertThrows(
                NestedServletException.class,
                () -> mockMvc.perform(post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is5xxServerError()));

        assertEquals("login - В логине не может быть пробелов", Objects.requireNonNull(exception.getMessage()).substring(108).trim());
    }

    @Test
    public void addUserFailEmailShouldGiveException() {
        user.setEmail("mail.ru");

        final NestedServletException exception = assertThrows(
                NestedServletException.class,
                () -> mockMvc.perform(post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is5xxServerError()));

        assertEquals("email - Почта не валидна", Objects.requireNonNull(exception.getMessage()).substring(108).trim());
    }

    @Test
    public void addUserEmptyEmailShouldGiveException() {
        user.setEmail("");

        final NestedServletException exception = assertThrows(
                NestedServletException.class,
                () -> mockMvc.perform(post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is5xxServerError()));

        assertEquals("email - Почта не валидна", Objects.requireNonNull(exception.getMessage()).substring(108).trim());
    }

    @Test
    public void addUserFailBirthdayShouldGiveException() {
        user.setBirthday(LocalDate.of(2025, 12, 23));

        final NestedServletException exception = assertThrows(
                NestedServletException.class,
                () -> mockMvc.perform(post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is5xxServerError()));

        assertEquals("birthday - Дата рождения не может быть в будущем", Objects.requireNonNull(exception.getMessage()).substring(108).trim());
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

        user.setId(1);
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
    public void updateFailUserShouldGiveException() {
        final NestedServletException exception = assertThrows(
                NestedServletException.class,
                () -> mockMvc.perform(put("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is5xxServerError()));

        assertEquals("Пользователь не найден", Objects.requireNonNull(exception.getMessage()).substring(108).trim());
    }

    @Test
    public void getAllUserShouldGive2() throws Exception {
        User userSecond = new User();
        userSecond.setEmail("asd@mail.ru");
        userSecond.setLogin("second");
        userSecond.setName("second");
        userSecond.setBirthday(LocalDate.of(2005, 1, 2));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userSecond))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        user.setId(1);
        userSecond.setId(2);

        mockMvc.perform(get("/users"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user, userSecond))));
    }

}