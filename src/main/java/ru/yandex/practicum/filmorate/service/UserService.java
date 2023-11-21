package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        userStorage.getUserById(user.getId());

        return userStorage.update(user);
    }

    public List<User> getFriends(Long id) {
        userStorage.getUserById(id);

        return userStorage.getFriends(id);
    }

    public void addFriend(Long id, Long friendId) {
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);

        userStorage.addFriend(id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);

        userStorage.removeFriend(id, friendId);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        userStorage.getUserById(id);
        userStorage.getUserById(otherId);

        return userStorage.getCommonFriends(id, otherId);
    }
}
