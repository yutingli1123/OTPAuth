package fans.goldenglow.otpauth.service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import fans.goldenglow.otpauth.dto.TokenResponse;
import fans.goldenglow.otpauth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
    private final NimbusJwtEncoder jwtEncoder;
    private final NimbusJwtDecoder jwtDecoder;

    @Autowired
    public TokenService(RedisTemplate<String, String> redisTemplate, UserService userService, @Value("${jwt.secret}") String jwtSecret) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        SecretKey secretKey = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
        jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    public void saveVerificationCode(String email, String verificationCode) {
        redisTemplate.opsForValue().set(VERIFICATION_CODE_PREFIX + email, verificationCode);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }

        return code.toString();
    }

    private JwtClaimsSet createTokenClaimSet(String userId, long expirationMinutes) {
        Instant now = Instant.now();
        return JwtClaimsSet.builder()
                .issuer(JWT_ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
                .subject(userId)
                .build();
    }

    public String createVerificationCode(String email) {
        String verificationCode = generateVerificationCode();

        // 保存到Redis，并设置过期时间
        redisTemplate.opsForValue().set(
                VERIFICATION_CODE_PREFIX + email,
                verificationCode,
                VERIFICATION_CODE_EXPIRATION,
                TimeUnit.MINUTES
        );

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

    private TokenResponse generateTokens(Long userId)
    {
        String userIdStr = userId.toString();
        JwtClaimsSet accessTokenClaimsSet = createTokenClaimSet(userIdStr,ACCESS_TOKEN_EXPIRATION);
        JwtClaimsSet refreshTokenClaimSet = createTokenClaimSet(userIdStr,REFRESH_TOKEN_EXPIRATION);

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaimsSet)).getTokenValue();
        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaimSet)).getTokenValue();
        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse generateTokens(String email) {
        User user = userService.createOrUpdateUser(email);

        Long userId = user.getId();

        return generateTokens(userId);
    }



    // 使用刷新令牌生成新的访问令牌
    public TokenResponse refreshToken(String refreshTokenValue) throws Exception {
        Jwt jwt = jwtDecoder.decode(refreshTokenValue);

        String userId = jwt.getSubject();

        if (!userService.existsById(Long.parseLong(userId))) {
            throw new Exception("Invalid user");
        }

        return generateTokens(Long.parseLong(userId));
    }
}
