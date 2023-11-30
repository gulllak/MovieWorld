package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
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

    public List<Film> getRecommendations(Long id) {
        return filmStorage.getRecommendations(id);
    }
}
