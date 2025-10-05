package userservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import userservice.enums.OTPType;
import userservice.exceptions.EmailDeliveryException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    @Value("${app.name}")
    private String appName;

    @Async
    public void sendOTP(String toEmail, String otpCode, OTPType otpType, LocalDateTime expiresAt) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(getSubject(otpType));

            Context context = new Context();
            context.setVariable("otpCode", otpCode);
            context.setVariable("otpType", otpType.getDisplayName());
            context.setVariable("appName", appName);
            context.setVariable("expiresAt", formatExpiryTime(expiresAt));
            context.setVariable("validityMinutes", otpType.getValidityInSeconds() / 60);

            String htmlContent = templateEngine.process("otp-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}, Type: {}", toEmail, otpType);

        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new EmailDeliveryException("Failed to send email: " + e.getMessage());
        }
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to " + appName + "!");

            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("appName", appName);

            String htmlContent = templateEngine.process("welcome-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetConfirmation(String toEmail, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Successful - " + appName);

            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("appName", appName);
            context.setVariable("resetTime", LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));

            String htmlContent = templateEngine.process("password-reset-confirmation", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password reset confirmation sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset confirmation to {}: {}", toEmail, e.getMessage());
        }
    }

    private String getSubject(OTPType otpType) {
        return switch (otpType) {
            case EMAIL_VERIFICATION -> "Verify Your Email - " + appName;
            case PASSWORD_RESET -> "Password Reset Code - " + appName;
            case LOGIN -> "Your Login Code - " + appName;
            case REGISTRATION -> "Complete Your Registration - " + appName;
            default -> "Verification Code - " + appName;
        };
    }

    private String formatExpiryTime(LocalDateTime expiresAt) {
        return expiresAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
    }
}