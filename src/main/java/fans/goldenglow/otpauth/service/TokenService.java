package fans.goldenglow.otpauth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import fans.goldenglow.otpauth.dto.TokenResponse;
import fans.goldenglow.otpauth.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenService {
    @Value("${verification.code.length}")
    private int VERIFICATION_CODE_LENGTH;
    @Value("${verification.code.expiration}")
    private long VERIFICATION_CODE_EXPIRATION;
    @Value("${jwt.iss}")
    private String JWT_ISSUER;
    @Value("${jwt.expiration.access_token}")
    private long ACCESS_TOKEN_EXPIRATION;
    @Value("${jwt.expiration.refresh_token}")
    private long REFRESH_TOKEN_EXPIRATION;


    private static final String VERIFICATION_CODE_PREFIX = "verification:";
    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;
    private final Algorithm algorithm;

    @Autowired
    public TokenService(RedisTemplate<String, String> redisTemplate, UserService userService, SecurityService securityService) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        algorithm = Algorithm.HMAC256(securityService.GetSecret().getEncoded());
    }

    private void saveVerificationCode(String email, String verificationCode) {
        redisTemplate.opsForValue().set(
                VERIFICATION_CODE_PREFIX + email,
                verificationCode,
                VERIFICATION_CODE_EXPIRATION,
                TimeUnit.MINUTES
        );
    }

    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }

        return code.toString();
    }

    private String generateToken(String userId, long expirationMinutes, List<String> scopes) {
        Instant now = Instant.now();
        return JWT
                .create()
                .withIssuer(JWT_ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
                .withSubject(userId)
                .withClaim("scope", String.join(" ", scopes))
                .sign(algorithm);
    }

    public String createVerificationCode(String email) {
        if (redisTemplate.hasKey(VERIFICATION_CODE_PREFIX + email)) {
            return null;
        }

        String verificationCode = generateVerificationCode();

        saveVerificationCode(email, verificationCode);

        return verificationCode;
    }

    public boolean validateVerificationCode(String email, String verificationCode) {
        String key = VERIFICATION_CODE_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode != null && storedCode.equals(verificationCode)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    private TokenResponse generateTokens(Long userId) {
        String userIdStr = userId.toString();

        String accessToken = generateToken(userIdStr, ACCESS_TOKEN_EXPIRATION, List.of("profile", "email"));
        String refreshToken = generateToken(userIdStr, REFRESH_TOKEN_EXPIRATION, List.of("refresh_token"));
        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse generateTokens(String email) {
        User user = userService.createOrUpdateUser(email);

        Long userId = user.getId();

        return generateTokens(userId);
    }

    public TokenResponse refreshToken(String refreshTokenValue) throws Exception {
        JWTVerifier jwtVerifier = JWT.require(algorithm).withIssuer(JWT_ISSUER).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(refreshTokenValue);

        if (!decodedJWT.getClaim("scope").asString().contains("refresh_token")) {
            throw new Exception("Invalid scope");
        }

        String userId = decodedJWT.getSubject();

        if (!userService.existsById(Long.parseLong(userId))) {
            throw new Exception("Invalid user");
        }

        return generateTokens(Long.parseLong(userId));
    }
}
