package back.courseback.coursesbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // rol sencillo, por ejemplo "ROLE_USER"
    @Column(nullable = false)
    private String rol = "ADMIN";

    /*
     * NO-ARGS CONSTRUCTOR:
     * This is an empty constructor that takes no parameters.
     * JPA (Hibernate) REQUIRES this to be present.
     * When fetching a user from the database, JPA uses this to create
     * a empty object first, before filling it with data.
     */
    public User() {
    }

    /*
     * PARAMETERIZED CONSTRUCTOR:
     * This allows you to create a new User object and set its
     * initial values (email, name, password, role) all in one line.
     * Example: new User("email@test.com", "Name", "Pass", "ADMIN")
     */
    public User(String email, String nombre, String password, String rol) {
        this.email = email;
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
