package fans.goldenglow.otpauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TokenRequest {
    @JsonProperty("email")
    public String email;
    @JsonProperty("verification_code")
    public String verificationCode;
}
