package fans.goldenglow.otpauth.repository;

import fans.goldenglow.otpauth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations and custom queries
 * on the {@link User} entity. This repository leverages Spring Data JPA
 * to interact with the database.
 * <p>
 * Methods provided by JpaRepository include basic CRUD operations,
 * as well as pagination and sorting capabilities. Additional custom
 * queries can be defined as needed.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
