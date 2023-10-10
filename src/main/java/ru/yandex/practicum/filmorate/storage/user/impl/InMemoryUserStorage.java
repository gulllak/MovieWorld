package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Integer id = 0;

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь");
        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            user.setFriends(users.get(user.getId()).getFriends());
            users.put(user.getId(), user);
            log.info("Данные пользователя изменены");
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return user;
    }

    @Override
    public User getUserById(int id) {
        if(!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    private Integer getNextId() {
        return ++id;
    }
}
