package fans.goldenglow.otpauth.controller;

import fans.goldenglow.otpauth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final TokenService tokenService;

    @Autowired
    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }
}
