package fans.goldenglow.otpauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class VerificationCode {
    private String verificationCode;
    private LocalDateTime createdAt;
}
