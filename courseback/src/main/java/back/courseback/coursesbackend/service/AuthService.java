package back.courseback.coursesbackend.service;

import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import back.courseback.coursesbackend.dto.LoginRequest;
import back.courseback.coursesbackend.dto.SignUpRequest;
import back.courseback.coursesbackend.model.User;
import back.courseback.coursesbackend.repository.UserRepository;
import back.courseback.coursesbackend.security.JwtTokenProvider;

/*
Diferences between spring or not spring
In short, without Spring, you write the code that 
builds your application's object graph. With Spring, 
you declare the rules for the graph, and Spring builds 
it for you.

Withoyt spring  You are responsible for:
Instantiation: Calling new on every single object.
Wiring: Passing the created objects into the 
constructors of other objects that need them.
Lifecycle Management: Deciding when these objects 
are created and destroyed

Spring's Dependency Injection framework automates 
all of this. You simply declare the dependencies in 
the constructor (like you did in AuthService), and 
Spring's ApplicationContext handles the entire 
creation and wiring process for you based on your 
@Bean definitions and @Component scans


*/

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;    
    private final JwtTokenProvider tokenProvider; 
    //from file JwtTokenProvider.java 
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Function that return a string and receives LoginRequest Object
    //With the values sending by the front end recently
    public String authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),       // usamos email como username
                    loginRequest.getPassword()
                )
            );
            return tokenProvider.generateToken(authentication);
        } catch (AuthenticationException e) {
            throw e; // Let controller handle invalid credentials (401)
        } catch (Exception e) {
            throw new RuntimeException("Error authenticating user", e);
        }
    }

    public User registerUser(SignUpRequest signUpRequest) {
        try {
            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                throw new RuntimeException("Email already in use");
            }

            User user = new User();
            user.setNombre(signUpRequest.getNombre());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            
            String rol = signUpRequest.getRol();
            if (rol == null || rol.isBlank()) {
                rol = "USER";
            }
            user.setRol(rol.toUpperCase());

            return userRepository.save(user);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error registering user", e);
        }
    }

        public User updateUser(Long id, SignUpRequest signUpRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setNombre(signUpRequest.getNombre());
        user.setEmail(signUpRequest.getEmail());

        if (signUpRequest.getPassword() != null && !signUpRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        }

        if (signUpRequest.getRol() != null && !signUpRequest.getRol().isBlank()) {
            user.setRol(signUpRequest.getRol().toUpperCase());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
    userRepository.deleteById(id);
}

    public List<User> findAll() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching users", e);
        }
    }
}