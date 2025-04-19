package fans.goldenglow.otpauth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${config.email.from}")
    private String emailFrom;
    @Value("${config.verification.code.expiration}")
    private int VERIFICATION_CODE_EXPIRATION;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerificationEmail(String email, String verificationCode) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(email);
            helper.setSubject("OTP Auth Verification Code");

            Context context = new Context();
            context.setVariable("verificationCode", verificationCode);
            context.setVariable("expirationMinutes", VERIFICATION_CODE_EXPIRATION);
            String html = templateEngine.process("email-verification", context);
            helper.setText(html, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send verification email", e);
        }
    }
}
