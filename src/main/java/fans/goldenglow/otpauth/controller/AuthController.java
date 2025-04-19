package fans.goldenglow.otpauth.controller;

import fans.goldenglow.otpauth.dto.EmailVerificationRequest;
import fans.goldenglow.otpauth.service.EmailService;
import fans.goldenglow.otpauth.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The {@code AuthController} class provides RESTful endpoints for handling authentication-related operations.
 * This controller is responsible for token validation and requesting email verification for users.
 * <p>
 * It interacts with the {@code TokenService} and {@code EmailService} to handle these functionalities.
 * <p>
 * The base API endpoint for this controller is {@code /api/v1/auth}.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final TokenService tokenService;
    private final EmailService emailService;

    /**
     * Constructor for the AuthController class.
     *
     * @param tokenService The TokenService instance to be used by this controller.
     * @param emailService The EmailService instance to be used by this controller.
     */
    @Autowired
    public AuthController(TokenService tokenService, EmailService emailService) {
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    /**
     * Validates a user's access token by checking if the token is valid and has not expired.
     *
     * @return A 200 OK response if the token is valid, or a 401 Unauthorized response if the token is invalid or expired.
     */
    @PostMapping
    public ResponseEntity<Void> validateToken() {
        return ResponseEntity.ok().build();
    }

    /**
     * Requests a verification code for a given email address.
     *
     * @param request The EmailVerificationRequest object containing the email address to request a verification code for.
     * @return A 200 OK response if the verification code was successfully sent, or a 400 Bad Request response if the email address is invalid.
     */
    @PostMapping("/request-verification")
    public ResponseEntity<Void> requestVerification(@Valid @RequestBody EmailVerificationRequest request) {
        String email = request.getEmail();

        String verificationCode = tokenService.createVerificationCode(email);

        if (verificationCode == null) {
            return ResponseEntity.badRequest().build();
        }

        emailService.sendVerificationEmail(email, verificationCode);

        return ResponseEntity.ok().build();
    }
}
