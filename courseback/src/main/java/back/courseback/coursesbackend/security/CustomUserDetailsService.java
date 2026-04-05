package back.courseback.coursesbackend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import back.courseback.coursesbackend.model.User;
import back.courseback.coursesbackend.repository.UserRepository;

//I used this file in SecurityConfig.java
// I used in CustomUserDetailsService.java file too

@Service
public class CustomUserDetailsService implements UserDetailsService {
    /*
     * final: The variable can be assigned a value only once. After
     * you give it a value (usually in the constructor), it cannot
     * be changed to point to a different object.
     */
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Spring llamará esto pasando el "username", nosotros usaremos email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new CustomUserDetails(user); // Used from CustomUserDetails.java
    }
}