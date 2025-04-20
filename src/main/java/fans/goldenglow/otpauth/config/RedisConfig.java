package fans.goldenglow.otpauth.config;

import fans.goldenglow.otpauth.dto.VerificationCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Configuration class for setting up Redis integration within the application.
 * <p>
 * This class is responsible for creating a RedisTemplate bean, which is used to
 * interact with a Redis data store. The RedisTemplate is configured to work with
 * keys of type String and values of type VerificationCode.
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, VerificationCode> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, VerificationCode> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
