package fans.goldenglow.otpauth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import fans.goldenglow.otpauth.dto.TokenResponse;
import fans.goldenglow.otpauth.dto.VerificationCode;
import fans.goldenglow.otpauth.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenService {
    @Value("${config.verification.code.length}")
    private int VERIFICATION_CODE_LENGTH;
    @Value("${config.verification.code.expiration}")
    private long VERIFICATION_CODE_EXPIRATION;
    @Value("${config.verification.code.resend_threshold}")
    private long RESEND_THRESHOLD;
    @Value("${config.jwt.iss}")
    private String JWT_ISSUER;
    @Value("${config.jwt.expiration.access_token}")
    private long ACCESS_TOKEN_EXPIRATION;
    @Value("${config.jwt.expiration.refresh_token}")
    private long REFRESH_TOKEN_EXPIRATION;

    private static final String VERIFICATION_CODE_PREFIX = "verification:";
    private final RedisTemplate<String, VerificationCode> redisTemplate;
    private final UserService userService;
    private final Algorithm algorithm;

    @Autowired
    public TokenService(RedisTemplate<String, VerificationCode> redisTemplate, UserService userService, SecurityService securityService) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        this.algorithm = Algorithm.HMAC256(securityService.GetSecret().getEncoded());
    }

    private void saveVerificationCode(String email, VerificationCode verificationCode) {
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

    private String generateToken(String userId, long expirationMinutes, String[] scopes) {
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
        VerificationCode verificationCodeObj = redisTemplate.opsForValue().get(VERIFICATION_CODE_PREFIX + email);
        if (verificationCodeObj != null) {
            LocalDateTime creationTime = verificationCodeObj.getCreatedAt();
            LocalDateTime expirationTime = creationTime.plusSeconds(RESEND_THRESHOLD);
            if (creationTime.isBefore(expirationTime)) {
                return null;
            } else {
                redisTemplate.delete(VERIFICATION_CODE_PREFIX + email);
            }
        }

        String verificationCode = generateVerificationCode();

        LocalDateTime now = LocalDateTime.now();

        saveVerificationCode(email, new VerificationCode(verificationCode, now));

        return verificationCode;
    }

    public boolean validateVerificationCode(String email, String verificationCode) {
        String key = VERIFICATION_CODE_PREFIX + email;
        VerificationCode verificationCodeObj = redisTemplate.opsForValue().get(key);

        String storedCode = null;
        if (verificationCodeObj != null) {
            storedCode = verificationCodeObj.getVerificationCode();
        }

        if (storedCode != null && storedCode.equals(verificationCode)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    private TokenResponse generateTokens(Long userId) {
        String userIdStr = userId.toString();

        String accessToken = generateToken(userIdStr, ACCESS_TOKEN_EXPIRATION, new String[]{"profile"});
        String refreshToken = generateToken(userIdStr, REFRESH_TOKEN_EXPIRATION, new String[]{"refresh_token"});

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
