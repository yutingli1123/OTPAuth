package fans.goldenglow.otpauth.service;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Service
public class JwtService {
    private final Algorithm algorithm;

    @Autowired
    public JwtService(SecurityService securityService) {
        this.algorithm = Algorithm.HMAC256(securityService.GetSecret().getEncoded());
    }
}
