package back.courseback.coursesbackend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String SECRET = "mySecretKeyTest1234567890123456789012345678901234567890"; // Must be at least 32 chars for HMAC256
    private static final long EXPIRATION_MS = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        // Manually instantiate since we are doing a pure unit test without Spring Context
        jwtTokenProvider = new JwtTokenProvider(SECRET, EXPIRATION_MS);
    }

    @Test
    void givenValidAuthentication_whenGenerateToken_thenReturnValidToken() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@domain.com");

        // Act
        String token = jwtTokenProvider.generateToken(authentication);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        
        // Also verify the generated token can be parsed to extract the correct username
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);
        assertThat(extractedUsername).isEqualTo("test@domain.com");
        
        // And verify it validates as true
        boolean isValid = jwtTokenProvider.validateToken(token);
        assertThat(isValid).isTrue();
    }

    @Test
    void givenInvalidToken_whenValidateToken_thenReturnFalse() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken("ey.invalid.token");

        // Assert
        assertThat(isValid).isFalse();
    }
}
