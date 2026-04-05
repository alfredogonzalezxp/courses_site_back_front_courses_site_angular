package back.courseback.coursesbackend.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

//I use this file in SecurityConfig.java

/*
 * @Component: This annotation marks the class as a Spring-managed component.
 * This means Spring will automatically detect this class and register it as a
 * bean in the application context.
 * In this case, it tells Spring that JwtAuthenticationFilter is a component
 * that should be managed by the Spring framework.
 * " You don't need to manually write new JwtAuthenticationFilter()"
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    // From JwtTokenProvider.java
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider,
            CustomUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /*
     * THE FILTER ENGINE: doFilterInternal
     * This method is the "engine" of your custom filter. Spring Security calls this
     * automatically for EVERY single incoming HTTP request.
     * 
     * @Override: Indicates we are providing the specific logic for
     * OncePerRequestFilter.
     * 
     * protected void: This method is internal to Spring Security. It does work
     * (checks
     * tokens) rather than returning a value.
     * 
     * @NonNull HttpServletRequest request: The incoming data from the user/browser.
     * It contains the URL, POST body, and crucially, the Headers where the JWT is.
     * 
     * @NonNull HttpServletResponse response: The outgoing package. If the token was
     * invalid, you could use this object to immediately send a "401 Unauthorized"
     * error back to the user, stopping the request here.
     * 
     * @NonNull FilterChain filterChain: The list of remaining security "doors". We
     * use
     * this at the end to pass the request along once we are done checking the JWT.
     * 
     * throws ServletException, IOException: Standard exceptions that web filters
     * might throw if something goes wrong with the HTTP connection.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // getJwtFromRequest is from a method below.
        String jwt = getJwtFromRequest(request);

        if (jwt != null && tokenProvider.validateToken(jwt)) {
            String username = tokenProvider.getUsernameFromToken(jwt);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            /*
             * auth.setDetails(...):
             * This line records extra details about the HTTP request, specifically
             * the user's Remote IP Address and their Session ID.
             * While not strictly required for JWT, this is a Spring Security
             * best practice for auditing, logging, and detecting suspicious activity.
             */
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            /*
             * SecurityContextHolder.getContext().setAuthentication(auth):
             * THIS IS THE MOST IMPORTANT LINE!
             * By placing our custom 'auth' token inside the SecurityContext, we are
             * officially telling Spring Security: "This user is now logged in and
             * fully authenticated for the duration of this specific request."
             */
            SecurityContextHolder.getContext().setAuthentication(auth);

        }

        /*
         * PASSING THE BATON:
         * This line marks the end of our custom JWT filter's job.
         * It tells Spring Security: "I am done checking the tokens. Please
         * pass this HTTP request and response to the NEXT filter in the security
         * chain (or to the Controller if there are no more filters)."
         * 
         * WARNING: If you delete this line, the request will get stuck here
         * forever and will never reach your API endpoints!
         */
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // formato: "Bearer <token>"
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}