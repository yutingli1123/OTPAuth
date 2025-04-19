package fans.goldenglow.otpauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a verification code with its creation timestamp.
 */
@AllArgsConstructor
@Getter
@Setter
public class VerificationCode implements Serializable {
    private String verificationCode;
    private LocalDateTime createdAt;
}
