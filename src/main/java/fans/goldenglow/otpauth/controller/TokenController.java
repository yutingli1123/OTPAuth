package fans.goldenglow.otpauth.controller;

import fans.goldenglow.otpauth.dto.RefreshTokenRequest;
import fans.goldenglow.otpauth.dto.TokenRequest;
import fans.goldenglow.otpauth.dto.TokenResponse;
import fans.goldenglow.otpauth.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/token")
public class TokenController {
    private final TokenService tokenService;

    @Autowired
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<TokenResponse> exchangeToken(@RequestBody TokenRequest token) {
        boolean verificationResult = tokenService.validateVerificationCode(token.getEmail(), token.getVerificationCode());
        if (verificationResult) {
            return ResponseEntity.ok(tokenService.generateTokens(token.getEmail()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        try {
            return ResponseEntity.ok(tokenService.refreshToken(refreshToken));
        } catch (Exception e) {
            log.error("Failed to refresh token", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
