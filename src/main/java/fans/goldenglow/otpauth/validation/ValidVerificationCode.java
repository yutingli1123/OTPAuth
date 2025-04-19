package fans.goldenglow.otpauth.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation to validate that a given field is a valid verification code.
 * The validation logic is implemented in the {@code VerificationCodeValidator} class.
 * <p>
 * This annotation can be applied to fields to ensure the verification code
 * adheres to a specific format or length as defined in the validator.
 * <p>
 * The default validation error message is "Invalid verification code".
 * <p>
 * The following parameters can be configured:
 * - {@code message}: Custom error message to be returned when the validation fails.
 * - {@code groups}: Allows grouping constraints to apply different validation rules.
 * - {@code payload}: Can be used to carry additional metadata about the validation failure.
 */
@Documented
@Constraint(validatedBy = VerificationCodeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVerificationCode {
    String message() default "Invalid verification code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
