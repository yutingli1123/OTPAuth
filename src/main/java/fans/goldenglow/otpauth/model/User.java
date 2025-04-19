package fans.goldenglow.otpauth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a user entity in the system. This class is mapped to the "users" table in the database.
 * It includes fields for identifying and managing user data such as email, creation timestamp, last login timestamp, and active status.
 * <p>
 * The {@code User} class uses JPA annotations to define the database schema mappings and utilizes Lombok annotations
 * for boilerplate code generation such as getters and setters.
 */
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column
    private boolean active = true;

    /**
     * Constructor for the User class.
     * @param email The email address of the user.
     */
    public User(String email) {
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }
}
