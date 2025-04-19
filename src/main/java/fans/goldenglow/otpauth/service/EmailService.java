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

/**
 * Service class for handling email-related operations.
 * <p>
 * This class is responsible for sending verification emails to users with a
 * templated body using Thymeleaf. It utilizes JavaMailSender for sending email
 * messages and integrates with a template engine to construct email contents.
 */
@Slf4j
@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${config.email.from}")
    private String emailFrom;
    @Value("${config.verification.code.expiration}")
    private long VERIFICATION_CODE_EXPIRATION;

    /**
     * Constructs an instance of the EmailService.
     * <p>
     * This constructor initializes the email service with a JavaMailSender for handling
     * email sending operations and a TemplateEngine for generating email content
     * based on templates.
     *
     * @param javaMailSender the JavaMailSender instance used to send emails
     * @param templateEngine the TemplateEngine instance used to process email templates
     */
    @Autowired
    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Sends a verification email with a specified verification code to the provided email address.
     * The email's content is generated using a Thymeleaf template and includes the verification code
     * and its expiration time.
     *
     * @param email the recipient's email address
     * @param verificationCode the verification code to be included in the email
     */
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
