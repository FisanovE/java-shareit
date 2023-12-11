package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    String email;

    @BeforeEach
    void setUp() {
        email = "user@mail.ru";
        User user = userRepository.save(new User(null, "user", email));
    }

    @Test
    void findByEmail() {
        Optional<User> actualUser = userRepository.findByEmail(email);

        assertTrue(actualUser.isPresent());
    }

    @AfterEach
    void deleteAll() {
        userRepository.deleteAll();
    }
}