package fans.goldenglow.otpauth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class SecurityService {
    private SecretKey jwtSecret;

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

    public SecretKey GetSecret() {
        if (jwtSecret == null) {
            jwtSecret = GenerateSecret();
        }
        return jwtSecret;
    }
}
