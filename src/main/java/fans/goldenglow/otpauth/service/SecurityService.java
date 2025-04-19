package fans.goldenglow.otpauth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

/**
 * Service class responsible for security-related operations.
 * <p>
 * This class is designed to manage cryptographic keys such as
 * the secret key used for JWT (JSON Web Token) operations.
 * It includes functionality to generate and retrieve the secret key.
 */
@Slf4j
@Service
public class SecurityService {
    private SecretKey jwtSecret;

    /**
     * Generates a secret key for use in JWT operations.
     * @return The generated secret key as a SecretKey object.
     */
    private SecretKey GenerateSecret() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm", e);
        }
        return null;
    }

    /**
     * Retrieves the secret key used for JWT operations.
     * @return The secret key as a SecretKey object.
     */
    public SecretKey getSecret() {
        if (jwtSecret == null) {
            jwtSecret = GenerateSecret();
        }
        return jwtSecret;
    }
}
