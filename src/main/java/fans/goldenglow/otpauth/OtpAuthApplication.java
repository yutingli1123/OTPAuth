package fans.goldenglow.otpauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the OTP Authentication service.
 * <p>
 * This class serves as the entry point for the Spring Boot application.
 * It is annotated with {@code @SpringBootApplication}, which marks it as
 * a configuration class and triggers auto-configuration, component scanning,
 * and additional configuration.
 */
@SpringBootApplication
public class OtpAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(OtpAuthApplication.class, args);
    }

}
