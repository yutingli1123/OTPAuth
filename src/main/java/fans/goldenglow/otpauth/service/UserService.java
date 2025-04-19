package fans.goldenglow.otpauth.service;

import fans.goldenglow.otpauth.model.User;
import fans.goldenglow.otpauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class responsible for managing user-related operations.
 * <p>
 * This class provides methods for retrieving and modifying user data,
 * including searching for users by email, checking if a user exists by ID,
 * retrieving users by ID, and creating or updating user information.
 * It interacts with the database through the {@link UserRepository}.
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    /**
     * Constructs an instance of the UserService.
     * <p>
     * This constructor initializes the service with a UserRepository, allowing
     * interaction with the underlying data storage for performing user-related operations.
     *
     * @param userRepository the repository instance used to interact with the User entity
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a user from the database based on the provided email address.
     *
     * @param email the email address of the user to retrieve
     * @return an {@code Optional} containing the {@code User} if found, or an empty {@code Optional} if no user exists with the given email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Checks if an entity with the given ID exists in the database.
     *
     * @param id the ID of the entity to check for existence
     * @return {@code true} if an entity with the given ID exists, {@code false} otherwise
     */
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Retrieves a user from the database based on the provided ID.
     *
     * @param id the ID of the user to retrieve
     * @return an {@code Optional} containing the {@code User} if found, or an empty {@code Optional} if no user exists with the given ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Creates a new user or updates an existing user based on the provided email address.
     * If a user with the given email already exists, their last login time is updated.
     * Otherwise, a new user entry is created with the current timestamp as the last login time.
     * The user data is then persisted to the database.
     *
     * @param email the email address of the user to create or update
     * @return the saved {@code User} entity after creation or update
     */
    @Transactional
    public User createOrUpdateUser(String email) {
        Optional<User> existingUser = findByEmail(email);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setLastLogin(LocalDateTime.now());
        } else {
            user = new User(email);
            user.setLastLogin(LocalDateTime.now());
        }

        return userRepository.save(user);
    }
}
