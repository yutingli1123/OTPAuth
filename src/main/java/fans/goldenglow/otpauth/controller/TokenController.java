package fans.goldenglow.otpauth.controller;

import fans.goldenglow.otpauth.dto.TokenRequest;
import fans.goldenglow.otpauth.dto.TokenResponse;
import fans.goldenglow.otpauth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/token")
public class TokenController {
    private final TokenService tokenService;

    @Autowired
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/exchange")
    public ResponseEntity<TokenResponse> exchangeToken(@RequestBody TokenRequest token) {
        boolean verificationResult = tokenService.validateVerificationCode(token.getEmail(), token.getVerificationCode());
        if (verificationResult) {
            return ResponseEntity.ok(tokenService.generateTokens(token.getEmail()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/refresh")
    public void refreshToken() {

    }
}
