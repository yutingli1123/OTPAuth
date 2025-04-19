package fans.goldenglow.otpauth.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

public class VerificationCodeValidator implements ConstraintValidator<ValidVerificationCode, String> {
    @Value("${verification.code.length}")
    private int VERIFICATION_CODE_LENGTH;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.length() == VERIFICATION_CODE_LENGTH;
    }
}
