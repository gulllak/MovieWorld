package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.Validate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    static int id = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody @Valid User user, BindingResult bindingResult) {
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        Validate.validate(bindingResult);
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь");
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User updatedUser, BindingResult bindingResult) {
        Validate.validate(bindingResult);

        if(users.containsKey(updatedUser.getId())) {
            users.put(updatedUser.getId(), updatedUser);
            log.info("Данные пользователя изменены");
        } else {
            throw new ValidationException("Пользователь не найден");
        }
        return updatedUser;
    }
}
