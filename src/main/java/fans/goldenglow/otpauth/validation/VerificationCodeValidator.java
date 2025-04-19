package fans.goldenglow.otpauth.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

/**
 * A validator that checks whether a provided string is a valid verification code.
 * <p>
 * This validator works in conjunction with the {@link ValidVerificationCode} annotation
 * to ensure that the given string matches the expected characteristics for a verification code.
 * Specifically, it ensures that the string is non-null and has the expected length.
 * <p>
 * The expected length of the verification code is configured using the
 * property {@code config.verification.code.length} from the application's configuration.
 * <p>
 * Implements the {@code ConstraintValidator} interface to provide the validation logic.
 * <p>
 * Methods:
 * - {@code isValid}: Evaluates whether the string meets the validation criteria.
 * <p>
 * Validation scenarios for invalid verification codes:
 * - The string is null.
 * - The string does not match the configured length for verification codes.
 */
public class VerificationCodeValidator implements ConstraintValidator<ValidVerificationCode, String> {
    @Value("${config.verification.code.length}")
    private int VERIFICATION_CODE_LENGTH;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.length() == VERIFICATION_CODE_LENGTH;
    }
}
