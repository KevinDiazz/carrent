package com.kevin.carrent.repository;

import com.kevin.carrent.entity.User;
import com.kevin.carrent.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {

        User user = new User();
        user.setName("Kevin");
        user.setEmail("kevin@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        userRepository.save(user);

        // Act
        Optional<User> result = userRepository.findByEmail("kevin@test.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("kevin@test.com", result.get().getEmail());

    }

    @Test
    void shouldReturnEmptyWhenUserDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("doesnotexist@test.com");

        assertTrue(result.isEmpty());
    }
}
