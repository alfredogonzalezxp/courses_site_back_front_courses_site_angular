# Backend Unit & Integration Tests Reference

This document contains a consolidated view of all the automated tests implemented for the Devcourses backend, leveraging **JUnit 5 (Jupiter)** and **Mockito**.

---

## 1. Repository Layer (Data Testing)
**File:** `src/test/java/back/courseback/coursesbackend/repository/UserRepositoryTest.java`

Uses `@DataJpaTest` to perform "slice" testing of the JPA layer. It uses an in-memory H2 database to verify that our custom repository methods work correctly without affecting the real PostgreSQL database.

```java
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
```

---

## 2. Service Layer (Business Logic Testing)
**File:** `src/test/java/back/courseback/coursesbackend/service/AuthServiceTest.java`

Uses plain **JUnit 5** with **Mockito** (`@ExtendWith(MockitoExtension.class)`). This is a "pure" unit test that does not load any Spring beans. Instead, it mocks the `UserRepository` and other dependencies to test business logic in isolation.

```java
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
```

---

## 3. Web Layer (Controller API Testing)
**File:** `src/test/java/back/courseback/coursesbackend/controller/AuthControllerTest.java`

Uses `@WebMvcTest` and `MockMvc` to test REST endpoints. It mocks the entire security filter chain (`addFilters = false`) and the `AuthService` to verify that the HTTP status codes and JSON payloads are correct.

```java
package back.courseback.coursesbackend.controller;

import back.courseback.coursesbackend.dto.LoginRequest;
import back.courseback.coursesbackend.dto.SignUpRequest;
import back.courseback.coursesbackend.model.User;
import back.courseback.coursesbackend.repository.UserRepository;
import back.courseback.coursesbackend.security.CustomUserDetailsService;
import back.courseback.coursesbackend.security.JwtAuthenticationFilter;
import back.courseback.coursesbackend.security.JwtTokenProvider;
import back.courseback.coursesbackend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass Spring Security filters for unit testing controllers
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidSignUpRequest_whenSignUp_thenReturnCreatedStatus() throws Exception {
        // Arrange
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setNombre("API User");
        signUpRequest.setEmail("api@domain.com");
        signUpRequest.setPassword("securepass");

        User createdUser = new User("api@domain.com", "API User", "hashedpass", "USER");
        createdUser.setId(1L);

        when(authService.registerUser(any(SignUpRequest.class))).thenReturn(createdUser);

        // Act & Assert
        mockMvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("api@domain.com"))
                .andExpect(jsonPath("$.nombre").value("API User"));
    }

    @Test
    void givenValidLoginRequest_whenSignIn_thenReturnJwt() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("api@domain.com");
        loginRequest.setPassword("securepass");

        String fakeToken = "eyJhbGciOiJIUzUxMiJ9.fakeTokenPayload";

        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(fakeToken);

        // Act & Assert
        mockMvc.perform(post("/api/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(fakeToken));
    }
}
```

---

## 4. Security Utility Testing
**File:** `src/test/java/back/courseback/coursesbackend/security/JwtTokenProviderTest.java`

Standard unit test to ensure the cryptographic logic for token generation and verification works exactly as expected.

```java
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
```
