package fans.goldenglow.otpauth.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request for email verification.
 * <p>
 * The email address is validated using the {@code @Email} annotation to ensure a valid format.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailVerificationRequest {
    @Email
    private String email;
}
