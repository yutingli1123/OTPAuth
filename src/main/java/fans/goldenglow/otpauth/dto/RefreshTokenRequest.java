package fans.goldenglow.otpauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request to refresh an authentication token.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefreshTokenRequest {
    @JsonProperty("refresh_token")
    private String refreshToken;
}
