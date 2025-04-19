package fans.goldenglow.otpauth.controller;

import fans.goldenglow.otpauth.model.User;
import fans.goldenglow.otpauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * The UserController class provides RESTful endpoints to handle operations related to user management.
 * This controller interacts with the UserService to retrieve user information.
 * <p>
 * The base API endpoint for this controller is /api/v1/user.
 * This class includes operations such as retrieving the authenticated user's information.
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    /**
     * Constructor for the UserController class.
     *
     * @param userService The UserService instance to be used by this controller.
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves information about the currently authenticated user.
     * The user information is extracted based on the JWT authentication token provided in the request.
     *
     * @param jwtAuthenticationToken The JwtAuthenticationToken containing the authentication information of the current user.
     * @return A ResponseEntity containing the User object if the user is found,
     * or a 404 Not Found response if the user is not found in the database.
     */
    @GetMapping("/me")
    public ResponseEntity<User> getSelf(JwtAuthenticationToken jwtAuthenticationToken) {
        Long userId = Long.parseLong(jwtAuthenticationToken.getToken().getSubject());
        Optional<User> user = userService.findById(userId);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
