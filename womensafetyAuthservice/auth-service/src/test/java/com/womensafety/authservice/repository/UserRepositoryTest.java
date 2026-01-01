package com.womensafety.authservice.repository;

import com.womensafety.authservice.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testExistsByUsername() {
        User user = new User(null, "john", "john@example.com", "password123", "ROLE_USER",false);
        userRepository.save(user);

        boolean exists = userRepository.existsByUsername("john");
        assertTrue(exists);
    }

    @Test
    void testFindByUsername() {
        User user = new User(null, "alice", "alice@example.com", "secret", "ROLE_USER",false);
        userRepository.save(user);

        Optional<User> result = userRepository.findByUsername("alice");
        assertTrue(result.isPresent());
        assertEquals("alice", result.get().getUsername());
    }

    @Test
    void testFindByUsername_NotFound() {
        Optional<User> result = userRepository.findByUsername("not_found");
        assertFalse(result.isPresent());
    }
}
