package back.courseback.coursesbackend.security;

import java.util.Collection;
import java.util.List;

import javax.swing.Spring;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import back.courseback.coursesbackend.model.User;

// UserDetails is an interface from Spring Security that 
// represents the user.
//java
/*import org.springframework.security.core.userdetails.UserDetails;
It is a core interface in Spring Security that represents the principal
 (the user) who is authenticated in the system. */

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // This is override because we implement UserDetails interface.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Un rol simple
        return List.of(new SimpleGrantedAuthority("ROL_" + user.getRol()));
    }

    // This is override because we implement UserDetails interface
    // if you realize getPassword and getUsername are override
    // methods because we implement UserDetails interface.
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // usamos el email como "username" para Spring Security
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public String getNombre() {
        return user.getNombre();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return user.getId();
    }
}