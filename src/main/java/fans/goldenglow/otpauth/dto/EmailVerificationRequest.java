package fans.goldenglow.otpauth.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request for email verification.
 * <p>
 * The email address is validated using the {@code @Email} annotation to ensure a valid format.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailVerificationRequest {
    @Email
    private String email;
}
