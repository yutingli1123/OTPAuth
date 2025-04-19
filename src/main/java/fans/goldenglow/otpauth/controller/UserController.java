package fans.goldenglow.otpauth.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import fans.goldenglow.otpauth.model.User;
import fans.goldenglow.otpauth.service.JwtService;
import fans.goldenglow.otpauth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final Algorithm algorithm;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.algorithm = jwtService.getAlgorithm();
    }

    @GetMapping("/me")
    public ResponseEntity<User> getSelf(@RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(jwtToken);
            Long userId = Long.parseLong(decodedJWT.getSubject());
            Optional<User> user = userService.findById(userId);
            return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (JWTVerificationException e) {
            log.error("Failed to verify JWT token", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
