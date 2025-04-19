package fans.goldenglow.otpauth.controller;

import fans.goldenglow.otpauth.dto.EmailVerificationRequest;
import fans.goldenglow.otpauth.service.EmailService;
import fans.goldenglow.otpauth.service.TokenService;
import fans.goldenglow.otpauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final TokenService tokenService;
    private final EmailService emailService;
    private final UserService userService;

    @Autowired
    public AuthController(TokenService tokenService, EmailService emailService, UserService userService) {
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> validateToken() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request-verification")
    public ResponseEntity<Void> requestVerification(@Validated @RequestBody EmailVerificationRequest request) {
        String email = request.getEmail();

        String verificationCode = tokenService.createVerificationCode(email);

        if (verificationCode == null) {
            return ResponseEntity.badRequest().build();
        }

        emailService.sendVerificationEmail(email, verificationCode);

        return ResponseEntity.ok().build();
    }
}
