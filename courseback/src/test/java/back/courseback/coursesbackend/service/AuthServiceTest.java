package back.courseback.coursesbackend.service;

import back.courseback.coursesbackend.dto.LoginRequest;
import back.courseback.coursesbackend.dto.SignUpRequest;
import back.courseback.coursesbackend.model.User;
import back.courseback.coursesbackend.repository.UserRepository;
import back.courseback.coursesbackend.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setNombre("Test User");
        signUpRequest.setEmail("test@domain.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setRol("USER");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@domain.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void givenValidSignUpRequest_whenRegisterUser_thenReturnSavedUser() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        
        User savedUser = new User("test@domain.com", "Test User", "hashedPassword", "USER");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = authService.registerUser(signUpRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@domain.com");
        assertThat(result.getPassword()).isEqualTo("hashedPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenExistingEmail_whenRegisterUser_thenThrowRuntimeException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerUser(signUpRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Email already in use");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenValidLoginRequest_whenAuthenticateUser_thenReturnJwtToken() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("mocked-jwt-token");

        // Act
        String token = authService.authenticateUser(loginRequest);

        // Assert
        assertThat(token).isEqualTo("mocked-jwt-token");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider, times(1)).generateToken(authentication);
    }
}
