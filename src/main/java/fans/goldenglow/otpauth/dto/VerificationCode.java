package fans.goldenglow.otpauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a verification code with its creation timestamp.
 */
@AllArgsConstructor
@Data
public class VerificationCode implements Serializable {
    private String verificationCode;
    private LocalDateTime createdAt;
}
