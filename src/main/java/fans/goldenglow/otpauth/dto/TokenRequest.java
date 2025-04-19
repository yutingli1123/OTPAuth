package fans.goldenglow.otpauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fans.goldenglow.otpauth.validation.ValidVerificationCode;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request for generating authentication tokens.
 */
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
