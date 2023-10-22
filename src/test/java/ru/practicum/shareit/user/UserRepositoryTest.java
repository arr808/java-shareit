package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldNotAddUserNoName() {
        User user = User.builder()
                .email("user@email.ru")
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    public void shouldNotAddUserNoEmail() {
        User user = User.builder()
                .name("user")
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    public void shouldNotAddUserDuplicateEmail() {
        User user = User.builder()
                .name("user")
                .email("user@email.ru")
                .build();

        User user2 = User.builder()
                .name("user2")
                .email("user@email.ru")
                .build();

        userRepository.save(user);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user2));
    }
}
