package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @Test
    void listUsers() {
        assertEquals(3, userDbStorage.findAll().size());
    }

    @Test
    void getUserById() {
        Optional<User> userOptional = Optional.ofNullable(userDbStorage.findById(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void createUser() {
        User user = new User();
        user.setEmail("email4");
        user.setLogin("login4");
        user.setBirthday(LocalDate.ofEpochDay(1985 - 5 - 5));
        user.setName("name4");
        userDbStorage.create(user);
        assertEquals(4, userDbStorage.findAll().size());
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("update");
        user.setLogin("login4");
        user.setBirthday(LocalDate.ofEpochDay(1985 - 5 - 5));
        user.setName("update");
        userDbStorage.update(user);
        assertEquals("update", userDbStorage.findById(1).getName());
    }

    @Test
    void addFriendPlusGetUserFriends() {
        User user = userDbStorage.findById(1);
        User friend = userDbStorage.findById(2);
        userDbStorage.addFriend(user, friend);
        assertEquals(1, userDbStorage.getFriends(user).size());
    }

    @Test
    void deleteFriend() {
        User user1 = userDbStorage.findById(1);
        User user2 = userDbStorage.findById(2);
        User user3 = userDbStorage.findById(3);
        userDbStorage.addFriend(user1, user2);
        userDbStorage.addFriend(user1, user3);
        userDbStorage.removeFriend(user1, user3);
        assertEquals(1, userDbStorage.getFriends(user1).size());
    }

    @Test
    void getCommonFriendList() {
        User user1 = userDbStorage.findById(1);
        User user2 = userDbStorage.findById(2);
        User user3 = userDbStorage.findById(3);
        userDbStorage.addFriend(user1, user2);
        userDbStorage.addFriend(user1, user3);
        userDbStorage.addFriend(user2, user3);
        List<User> friends = userDbStorage.getCommonFriends(user1, user2);
        assertEquals(3, friends.get(0).getId());
    }
}
