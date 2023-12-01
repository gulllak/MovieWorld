package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    User create(User user);

    User update(User user);

    User getUserById(Long id);

    List<User> getFriends(Long id);

    void addFriend(Long id, Long friendId);

    void removeFriend(Long id, Long friendId);

    List<User> getCommonFriends(Long id, Long otherId);

    void remove(Long userId);
}
