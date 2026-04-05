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
