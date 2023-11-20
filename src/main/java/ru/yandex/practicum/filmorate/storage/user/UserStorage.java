package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    User create(User user);

    User update(User user);

    User getUserById(int id);

    List<User> getFriends(int id);

    void addFriend(int id, int friendId);

    void removeFriend(int id, int friendId);

    List<User> getCommonFriends(int id, int otherId);
}
