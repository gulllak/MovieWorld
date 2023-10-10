package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        Set<Integer> user = userStorage.getUserById(id).getFriends();
        Set<Integer> otherUser = userStorage.getUserById(otherId).getFriends();

        Set<Integer> commonFriendsId = user.stream()
                .filter(otherUser::contains)
                .collect(Collectors.toSet());

        List<User> commonFriends = new ArrayList<>();
        for (Integer userId : commonFriendsId) {
            commonFriends.add(userStorage.getUserById(userId));
        }

        return commonFriends;
    }

    public void addFriend(int id, int friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }


    public List<User> getFriends(int id) {
        User user = userStorage.getUserById(id);
        List<User> friends = new ArrayList<>();
        for (int friendId : user.getFriends()) {
            friends.add(userStorage.getUserById(friendId));
        }
        return friends;
    }

    public void removeFriend(int id, int friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }
}
