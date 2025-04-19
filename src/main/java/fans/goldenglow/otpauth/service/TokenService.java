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

/**
 * Service class responsible for handling token management and verification code generation.
 * This class provides functionality for the creation and validation of verification codes,
 * along with generating access and refresh tokens for user authentication.
 */
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

    /**
     * Constructs a TokenService instance with dependencies injected.
     *
     * @param redisTemplate the RedisTemplate used for handling verification code storage and retrieval
     * @param userService the UserService responsible for user management operations
     * @param securityService the SecurityService used to provide cryptographic utilities for token signing
     */
    @Autowired
    public TokenService(RedisTemplate<String, VerificationCode> redisTemplate, UserService userService, SecurityService securityService) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        this.algorithm = Algorithm.HMAC256(securityService.getSecret().getEncoded());
    }

    /**
     * Stores a verification code associated with the given email in a Redis cache.
     * The code is stored with a predefined expiration time to ensure validity within a limited period.
     *
     * @param email the email address to associate with the verification code
     * @param verificationCode the verification code object containing the code and its creation timestamp
     */
    private void saveVerificationCode(String email, VerificationCode verificationCode) {
        redisTemplate.opsForValue().set(
                VERIFICATION_CODE_PREFIX + email,
                verificationCode,
                VERIFICATION_CODE_EXPIRATION,
                TimeUnit.MINUTES
        );
    }

    /**
     * Generates a random numeric verification code of a predefined length.
     * The length of the code is determined by the VERIFICATION_CODE_LENGTH field.
     *
     * @return a string representing the generated verification code
     */
    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }

        return code.toString();
    }

    /**
     * Generates a signed JWT (JSON Web Token) for the given user with specified expiration time and scopes.
     *
     * @param userId the unique identifier of the user for whom the token is generated
     * @param expirationMinutes the duration, in minutes, for which the token will remain valid
     * @param scopes an array of strings representing the scopes or permissions associated with the token
     * @return a string representing the generated JWT
     */
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

    /**
     * Creates a verification code for a given email address. If a code already exists and is within
     * the resend threshold, no new code will be generated. Otherwise, an existing code is deleted,
     * and a new one is generated and stored.
     *
     * @param email the email address for which the verification code is generated
     * @return the generated verification code as a string, or null if a code already exists and is
     * within the resend threshold
     */
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

    /**
     * Validates the provided verification code against the stored code associated with the given email.
     * If the code is valid, it is removed from the storage to prevent reuse.
     *
     * @param email the email address associated with the verification code
     * @param verificationCode the verification code to be validated
     * @return true if the provided verification code matches the stored code, false otherwise
     */
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

    /**
     * Generates access and refresh tokens for the specified user ID.
     * The access token is created with profile-related scopes, and the refresh token
     * is generated with a scope specific to refresh token functionality.
     * Both tokens have predefined expiration times.
     *
     * @param userId the unique identifier of the user for whom the tokens are generated
     * @return a {@link TokenResponse} object containing the access token and refresh token
     */
    private TokenResponse generateTokens(Long userId) {
        String userIdStr = userId.toString();

        String accessToken = generateToken(userIdStr, ACCESS_TOKEN_EXPIRATION, new String[]{"profile"});
        String refreshToken = generateToken(userIdStr, REFRESH_TOKEN_EXPIRATION, new String[]{"refresh_token"});

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * Generates access and refresh tokens for a user identified by their email address.
     * This method ensures the user's existence by creating or updating the user based on the provided email.
     * The returned tokens include an access token with profile-related scopes and a refresh token,
     * both with predefined expiration times.
     *
     * @param email the email address of the user for whom the tokens are generated
     * @return a {@link TokenResponse} object containing the generated access token and refresh token
     */
    public TokenResponse generateTokens(String email) {
        User user = userService.createOrUpdateUser(email);

        Long userId = user.getId();

        return generateTokens(userId);
    }

    /**
     * Refreshes and generates new access and refresh tokens using the provided refresh token.
     * This method validates the refresh token's scope and verifies the existence of the associated user.
     *
     * @param refreshTokenValue the refresh token to be validated and used for generating new tokens
     * @return a {@link TokenResponse} object containing the newly generated access token and refresh token
     * @throws Exception if the refresh token has an invalid scope or if the associated user does not exist
     */
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
