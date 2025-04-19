package fans.goldenglow.otpauth.config;

import fans.goldenglow.otpauth.dto.VerificationCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, VerificationCode> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, VerificationCode> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
