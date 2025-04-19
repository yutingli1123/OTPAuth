package fans.goldenglow.otpauth.controller;

import fans.goldenglow.otpauth.dto.RefreshTokenRequest;
import fans.goldenglow.otpauth.dto.TokenRequest;
import fans.goldenglow.otpauth.dto.TokenResponse;
import fans.goldenglow.otpauth.service.TokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TokenController acts as a REST controller for managing authentication tokens.
 * It provides endpoints for exchanging and refreshing tokens, integrating with the TokenService for core logic.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth/token")
public class TokenController {
    private final TokenService tokenService;

    /**
     * Constructor for the TokenController class.
     * @param tokenService The TokenService instance to be used by this controller.
     */
    @Autowired
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Exchanges a verification code for authentication tokens.
     * This method validates the provided email and verification code. If the validation succeeds,
     * it generates and returns a new access token and refresh token. If the validation fails,
     * it responds with a bad request status.
     *
     * @param token The TokenRequest object containing the user's email and verification code.
     * @return A ResponseEntity containing a TokenResponse with the generated tokens if the
     *         verification is successful, or a bad request status if verification fails.
     */
    @PostMapping
    public ResponseEntity<TokenResponse> exchangeToken(@Valid @RequestBody TokenRequest token) {
        boolean verificationResult = tokenService.validateVerificationCode(token.getEmail(), token.getVerificationCode());
        if (verificationResult) {
            return ResponseEntity.ok(tokenService.generateTokens(token.getEmail()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Refreshes the authentication tokens based on the provided refresh token.
     * This endpoint accepts a refresh token and validates it using the TokenService.
     * If valid, it returns a new access token and refresh token. If validation fails,
     * a bad request response is returned.
     *
     * @param refreshTokenRequest The request object containing the refresh token to be validated and used for generating new tokens.
     * @return A ResponseEntity containing a TokenResponse object with the new tokens if the operation is successful,
     *         or a bad request response if the token provided is invalid or the operation fails.
     */
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
