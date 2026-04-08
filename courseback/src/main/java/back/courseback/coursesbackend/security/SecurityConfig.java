package back.courseback.coursesbackend.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @Configuration: Marks this class as a source of bean definitions for the
 *                 Spring context.
 * @EnableWebSecurity: Disables default Spring Security and enables our custom
 *                     web security configuration.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Component that loads user data from the database.
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * Our custom filter that catches requests, checks for a JWT, and logs the user
     * in.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * CONSTRUCTOR INJECTION
     * Where does the jwtAuthenticationFilter come from?
     * It does NOT come from the HTTP request!
     * Because the JwtAuthenticationFilter class has @Component, Spring creates ONE
     * instance of it when the application starts up. Spring then "injects" that
     * single instance directly into this constructor so we can attach it to the
     * security chain below.
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * PASSWORD ENCODER BEAN
     * Defines exactly what hashing algorithm to use globally (BCrypt).
     * By declaring it as a @Bean, we can inject it anywhere in the app (like in
     * AuthService).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AUTHENTICATION PROVIDER (The Login Robot)
     * Assembles the tool that handles database-backed login logic.
     * It uses the UserDetailsService to find the user, and the PasswordEncoder to
     * verify the hash.
     * The actual comparison logic happens automatically inside Spring.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /**
     * AUTHENTICATION MANAGER BEAN
     * The main interface for authenticating requests. We tell it to use the
     * DaoAuthenticationProvider
     * we configured above. This manager is later injected into the AuthController.
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    /**
     * SECURITY FILTER CHAIN (The Core Security Rules)
     * This defines the actual rules for every incoming HTTP request.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {

        http
                // Disables CSRF protection (safe to disable because we use stateless JWTs, not
                // cookies)
                .csrf(csrf -> csrf.disable())

                // Enables CORS and tells it to use our configured rules (defined below)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configures the app to be STATELESS (no server-side sessions, fully relying on
                // tokens)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Defines which URLs are public and which require authentication
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (Login, Signup, Health checks)
                        .requestMatchers("/api/signin", "/api/signup", "/api/health").permitAll()

                        // User management endpoints restricted to administrators
                        .requestMatchers("/api/users", "/api/users/**").hasRole("ADMIN")

                        // Allows browsers to send 'OPTIONS' pre-flight checks before complex CORS
                        // requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // All other endpoints require the user to be fully authenticated
                        .anyRequest().authenticated());
        /*
         * ADDING OUR JWT FILTER TO THE CHAIN
         * 
         * Argument 1 (jwtAuthenticationFilter):
         * This is the INSTANCE of our filter. We tell Spring,
         * 
         * "Execute THIS specific object."
         * 
         * Argument 2 (UsernamePasswordAuthenticationFilter.class):
         * This is a CLASS LITERAL (a Blueprint/Marker). We are NOT passing an object
         * here.
         * We tell Spring: "Look through your security chain, find where the standard
         * UsernamePasswordAuthenticationFilter is supposed to be, and insert my custom
         * JWT filter exactly one step BEFORE it."
         */
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Builds and returns the final security firewall
        return http.build();
    }

    /**
     * CORS CONFIGURATION SOURCE
     * Defines the "guest list" of which frontend applications can talk to this
     * backend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allowed domains (Angular on 4200, Vue/Vite on 5173)
        config.setAllowedOrigins(
                List.of("http://localhost:4200", "http://localhost:5173", "https://alfredogonzalezxp.github.io"));

        // Allowed HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers
        config.setAllowedHeaders(List.of("*"));

        // Allow credentials (like JWT headers) to be sent across origins
        config.setAllowCredentials(true);

        // Apply these rules to every endpoint (/**) in our API
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}