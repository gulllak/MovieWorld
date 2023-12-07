package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserStorageTest {
    private final UserDbStorage userStorage;

    User user = User.builder()
            .id(1L)
            .email("user@email.ru")
            .name("vanya123")
            .login("Ivan Petrov")
            .birthday(LocalDate.of(1990, 1, 1))
            .build();

    User user1 = User.builder()
            .id(2L)
            .email("user1@email.ru")
            .name("Petya")
            .login("Petyyya")
            .birthday(LocalDate.of(1994, 2, 1))
            .build();

    User user2 = User.builder()
            .id(3L)
            .email("user2@email.ru")
            .name("Sasha Petrov")
            .login("Sasha")
            .birthday(LocalDate.of(1970, 1, 1))
            .build();

    @Test
    public void testFindUserById() {
        userStorage.create(user);

        User savedUser = userStorage.getUserById(1L);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void testFindUserByInvalidId() {
        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(
                EntityNotFoundException.class, () -> userStorage.getUserById(1L));
        Assertions.assertEquals("Пользователя c id 1 отсутствует", entityNotFoundException.getMessage());
    }

    @Test
    public void testFindAll() {
        userStorage.create(user);
        userStorage.create(user1);

        List<User> users = userStorage.findAll();

        Assertions.assertEquals(List.of(user, user1), users);
    }

    @Test
    public void testCreate() {
        userStorage.create(user);

        User getUser = userStorage.getUserById(1L);
        Assertions.assertEquals(user, getUser);
    }

    @Test
    public void testUpdate() {
        userStorage.create(user);

        user.setLogin("update");
        userStorage.update(user);

        User getUser = userStorage.getUserById(1L);
        Assertions.assertEquals(user, getUser);
    }

    @Test
    public void testAddFriendAndRemove() {
        userStorage.create(user);
        Assertions.assertEquals(new ArrayList<>(), userStorage.getFriends(1L));

        userStorage.create(user1);
        userStorage.addFriend(1L, 2L);
        Assertions.assertEquals(List.of(user1), userStorage.getFriends(1L));
        Assertions.assertEquals(new ArrayList<>(), userStorage.getFriends(2L));

        userStorage.addFriend(2L, 1L);
        Assertions.assertEquals(List.of(user1), userStorage.getFriends(1L));
        Assertions.assertEquals(List.of(user), userStorage.getFriends(2L));

        userStorage.removeFriend(1L, 2L);
        userStorage.removeFriend(2L, 1L);

        Assertions.assertEquals(new ArrayList<>(), userStorage.getFriends(1L));
        Assertions.assertEquals(new ArrayList<>(), userStorage.getFriends(2L));
    }

    @Test
    public void testAddFriendSecondTimeShouldGiveException() {
        userStorage.create(user);
        userStorage.create(user1);
        userStorage.addFriend(1L, 2L);
        EntityAlreadyExistException entityAlreadyExistException = Assertions.assertThrows(
                EntityAlreadyExistException.class, () -> userStorage.addFriend(1L, 2L));
        Assertions.assertEquals("Дружба уже существует", entityAlreadyExistException.getMessage());
    }

    @Test
    public void getCommonFriends() {
        userStorage.create(user);
        userStorage.create(user1);
        userStorage.create(user2);

        userStorage.addFriend(1L, 2L);
        userStorage.addFriend(1L, 3L);
        userStorage.addFriend(2L, 3L);

        Assertions.assertEquals(List.of(user2), userStorage.getCommonFriends(1L, 2L));
    }
}