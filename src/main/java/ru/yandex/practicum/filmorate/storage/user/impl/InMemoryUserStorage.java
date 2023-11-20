package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return user;
    }

    @Override
    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public List<User> getFriends(int id) {
        List<User> friends = new ArrayList<>();
        for (int friendId : getUserById(id).getFriends()) {
            friends.add(getUserById(friendId));
        }
        return friends;
    }

    @Override
    public void addFriend(int id, int friendId) {
        getUserById(id).getFriends().add(friendId);
        getUserById(friendId).getFriends().add(id);
    }

    @Override
    public void removeFriend(int id, int friendId) {
        getUserById(id).getFriends().remove(friendId);
        getUserById(friendId).getFriends().remove(id);
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        Set<Integer> user = getUserById(id).getFriends();
        Set<Integer> otherUser = getUserById(otherId).getFriends();

        Set<Integer> commonFriendsId = user.stream()
                .filter(otherUser::contains)
                .collect(Collectors.toSet());

        List<User> commonFriends = new ArrayList<>();
        for (Integer userId : commonFriendsId) {
            commonFriends.add(getUserById(userId));
        }

        return commonFriends;
    }

    private Integer getNextId() {
        return ++id;
    }
}
