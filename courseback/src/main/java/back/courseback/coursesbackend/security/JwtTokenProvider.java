package back.courseback.coursesbackend.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

/*
 * JwtTokenProvider is the Core Engine (or Factory) for JWTs.
 * 
 * DESIGN PURPOSE:
 * It handles all the cryptographic operations related to JSON Web Tokens.
 * It does NOT handle HTTP requests or responses directly; its job is to
 * serve the AuthController and JwtAuthenticationFilter.
 * 
 * MAIN RESPONSIBILITIES:
 * 1. Generate Tokens (create identifiers for users).
 * 2. Validate Tokens (check signatures and expiration).
 * 3. Extract Information (read who the user is from the token).
 */
/*
 * @Component:
 * This annotation marks this class as a "Spring Bean".
 * 
 * WHAT IT DOES:
 * 1. Spring detects this class automatically during startup (Component Scanning).
 * 2. It creates an INSTANCE of this class and manages its lifecycle.
 * 3. It injects the values from application.properties (jwt.secret, etc.) into the constructor.
 * 4. It makes this instance available to be injected into other classes 
 *    (like AuthController and JwtAuthenticationFilter) automatically.
 * 
 * Without this annotation, you would have to manually create 'new JwtTokenProvider(...)'
 * everywhere, which is redundant and hard to manage.
 */
@Component
public class JwtTokenProvider {

    private final Algorithm algorithm;
    private final long jwtExpirationMs;

    /*
     * Constructor:
     * - Loads the SECRET key from application.properties (jwt.secret).
     * - Loads the EXPIRATION time from application.properties (jwt.expiration-ms).
     * - Initializes the HMAC256 algorithm with the secret.
     * 
     * @Value("${jwt.expiration-ms}"):
     * This tells Spring to go to your 'application.properties' file,
     * find the line 'jwt.expiration-ms=3600000' (or whatever value you set),
     * and inject that number into the 'jwtExpirationMs' variable.
     * This way, you can change the expiration time without recompiling the code.
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long jwtExpirationMs) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /*
     * generateToken:
     * - Called by AuthController after successful login.
     * - Creates a new token containing:
     * - Subject: The user's email/username.
     * - IssuedAt: Current time.
     * - ExpiresAt: Current time + expirationMs.
     * - Signature: Signed with our secret algorithm.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName(); // aquí será email
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        /*
         * THE JWT BUILDER:
         * Uses the Builder Pattern to construct the token step-by-step.
         * 
         * JWT.create() -> Start building a new token.
         * .withSubject() -> Sets the 'who' (the payload 'sub' claim). We use email.
         * .withIssuedAt() -> Sets the creation time (the 'iat' claim).
         * .withExpiresAt() -> Sets the expiration time (the 'exp' claim).
         * 
         * .sign(algorithm) -> THE MOST IMPORTANT STEP.
         * It mathematically "locks" the payload using your secret key.
         * If a hacker tries to modify the username or expiry date inside the
         * token, this signature will break and the filter will reject it.
         */
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(algorithm);
    }

    /*
     * getUsernameFromToken:
     * - Called by JwtAuthenticationFilter to know WHO is sending the request.
     * - Decodes the token and extracts the "Subject" field.
     */
    public String getUsernameFromToken(String token) {
        DecodedJWT decoded = JWT.require(algorithm).build().verify(token);
        return decoded.getSubject();
    }

    /*
     * validateToken:
     * - Called by JwtAuthenticationFilter to ensure the token is valid.
     * - Checks:
     * 1. Is the signature correct? (Was it unmodified?)
     * 2. Is the token expired?
     * - Returns true if valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}