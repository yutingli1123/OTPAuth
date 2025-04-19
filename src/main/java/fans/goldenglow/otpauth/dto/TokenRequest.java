package fans.goldenglow.otpauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fans.goldenglow.otpauth.validation.ValidVerificationCode;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TokenRequest {
    @JsonProperty("email")
    @Email
    private String email;
    @JsonProperty("verification_code")
    @ValidVerificationCode
    private String verificationCode;
}
