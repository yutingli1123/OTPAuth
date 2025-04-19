package fans.goldenglow.otpauth.config;

import fans.goldenglow.otpauth.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasScope;

/**
 * Configuration class for setting up the security aspects of the application.
 * <p>
 * This class is annotated with @Configuration and @EnableWebSecurity, signifying
 * that it is a configuration component in Spring Security. It defines the security
 * filter chain and JWT decoder to handle authentication and request authorization.
 * <p>
 * Responsibilities include:
 * - Defining and configuring the security filter chain for handling HTTP security.
 * - Setting up rules for request authorization based on request matchers and scopes.
 * - Configuring stateless session management for the application.
 * - Providing a JwtDecoder bean configured with a secret key from the SecurityService.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final SecurityService securityService;

    @Autowired
    public SecurityConfig(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth").authenticated()
                        .requestMatchers("/api/v1/auth/**", "/public/**", "/error/**").permitAll()
                        .requestMatchers("/api/v1/user/**").access(hasScope("profile"))
                        .anyRequest().denyAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))
                )
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(securityService.getSecret()).build();
    }
}