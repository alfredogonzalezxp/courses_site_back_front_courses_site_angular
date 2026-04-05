package back.courseback.coursesbackend.repository;

import back.courseback.coursesbackend.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Automatically runs before each test to clean slate and seed data
        userRepository.deleteAll();
        testUser = new User("test@domain.com", "Test Boy", "password123", "USER");
        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void givenExistingEmail_whenFindByEmail_thenReturnUserObj() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("test@domain.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getNombre()).isEqualTo("Test Boy");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@domain.com");
    }

    @Test
    void givenNonExistingEmail_whenFindByEmail_thenReturnEmptyOptional() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("nobody@domain.com");

        // Assert
        assertThat(foundUser).isNotPresent();
    }

    @Test
    void givenExistingEmail_whenExistsByEmail_thenReturnTrue() {
        // Act
        boolean exists = userRepository.existsByEmail("test@domain.com");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void givenNonExistingEmail_whenExistsByEmail_thenReturnFalse() {
        // Act
        boolean exists = userRepository.existsByEmail("nobody@domain.com");

        // Assert
        assertThat(exists).isFalse();
    }
}
