package back.courseback.coursesbackend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import back.courseback.coursesbackend.model.User;

/*
This code defines an interface that acts as your data 
access layer for User entities. You don't have to write 
the implementation class yourself; Spring creates it for 
you at runtime.

public interface UserRepository  This declares a standard
 Java interface.

extends JpaRepository<User, Long>: This is where the 
magic happens. By extending JpaRepository, your 
UserRepository automatically inherits a full set of 
standard CRUD (Create, Read, Update, Delete) methods, 
such as save(), findById(), findAll(), and deleteById().

The <User, Long> part tells Spring Data JPA two 
things:
User: This repository is responsible for managing User 
entities.
Long: The primary key (@Id) of the User entity is of 
type Long

*/

public interface UserRepository extends JpaRepository<User, Long> {
    /*
     * findByEmail returns an Optional<User>.
     * 
     * Optional is a container that may or may not hold a User object.
     * - If the email exists in the DB, it returns the User.
     * - If the email DOES NOT exist, it returns an empty Optional.
     * 
     * This prevents NullPointerExceptions and forces the developer 
     * to handle the "user not found" scenario.
     */
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}