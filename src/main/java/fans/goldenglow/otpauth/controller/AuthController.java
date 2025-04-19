package fans.goldenglow.otpauth.controller;

import fans.goldenglow.otpauth.dto.EmailVerificationRequest;
import fans.goldenglow.otpauth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final TokenService tokenService;

    @Autowired
    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
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

//        emailService.sendVerificationEmail(email, verificationCode);

        return ResponseEntity.ok().build();
    }
}
