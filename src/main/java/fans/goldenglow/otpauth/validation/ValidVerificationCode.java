package fans.goldenglow.otpauth.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = VerificationCodeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVerificationCode {
    String message() default "Invalid verification code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
