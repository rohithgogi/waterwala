package userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import userservice.enums.OTPStatus;
import userservice.enums.OTPType;
import userservice.model.OTP;
import userservice.repository.OTPRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPService {
    private final OTPRepository otpRepository;
    private final SmsService smsService;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public void sendEmailVerificationOTP(String email) {
        String otpCode = generateOTP();
        OTP otp = createOTP(email, null, otpCode, OTPType.EMAIL_VERIFICATION);
        otpRepository.save(otp);

        // Send email asynchronously
        try {
            emailService.sendOTP(email, otpCode, OTPType.EMAIL_VERIFICATION, otp.getExpiresAt());
            log.info("Email verification OTP sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send email verification OTP to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public void sendPhoneVerificationOTP(String phone) {
        String otpCode = generateOTP();
        OTP otp = createOTP(null, phone, otpCode, OTPType.PHONE_VERIFICATION);
        otpRepository.save(otp);

        // Send SMS
        try {
            smsService.sendOTP(phone, otpCode, OTPType.PHONE_VERIFICATION);
            log.info("Phone verification OTP sent to: {}", phone);
        } catch (Exception e) {
            log.error("Failed to send phone verification OTP to {}: {}", phone, e.getMessage());
            throw new RuntimeException("Failed to send OTP SMS", e);
        }
    }

    public void sendLoginOTP(String phone) {
        String otpCode = generateOTP();
        OTP otp = createOTP(null, phone, otpCode, OTPType.LOGIN);
        otpRepository.save(otp);

        // Send SMS
        try {
            smsService.sendOTP(phone, otpCode, OTPType.LOGIN);
            log.info("Login OTP sent to: {}", phone);
        } catch (Exception e) {
            log.error("Failed to send login OTP to {}: {}", phone, e.getMessage());
            throw new RuntimeException("Failed to send OTP SMS", e);
        }
    }

    public void passwordResetOTP(String phone) {
        String otpCode = generateOTP();
        OTP otp = createOTP(null, phone, otpCode, OTPType.PASSWORD_RESET);
        otpRepository.save(otp);

        // Send SMS
        try {
            smsService.sendOTP(phone, otpCode, OTPType.PASSWORD_RESET);
            log.info("Password reset OTP sent to: {}", phone);
        } catch (Exception e) {
            log.error("Failed to send password reset OTP to {}: {}", phone, e.getMessage());
            throw new RuntimeException("Failed to send OTP SMS", e);
        }
    }

    public boolean verifyOTP(String contact, String otpCode, OTPType type) {
        Optional<OTP> otpOptional = findLatestOTP(contact, type);

        if (otpOptional.isEmpty()) {
            log.warn("No OTP found for contact: {}, type: {}", contact, type);
            return false;
        }

        OTP otp = otpOptional.get();

        // Already verified
        if (otp.getStatus() == OTPStatus.VERIFIED) {
            log.info("OTP already verified for contact: {}", contact);
            return true;
        }

        // Check expiration
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otp.setStatus(OTPStatus.EXPIRED);
            otpRepository.save(otp);
            log.warn("OTP expired for contact: {}", contact);
            return false;
        }

        // Check max attempts
        if (otp.getAttemptCount() >= 3) {
            otp.setStatus(OTPStatus.FAILED);
            otpRepository.save(otp);
            log.warn("Max OTP attempts reached for contact: {}", contact);
            return false;
        }

        // Increment attempt count
        otp.setAttemptCount(otp.getAttemptCount() + 1);

        // Verify OTP code
        if (otp.getOtpCode().equals(otpCode)) {
            otp.setStatus(OTPStatus.VERIFIED);
            otp.setVerifiedAt(LocalDateTime.now());
            otpRepository.save(otp);
            log.info("OTP verified successfully for contact: {}", contact);
            return true;
        } else {
            if (otp.getAttemptCount() >= 3) {
                otp.setStatus(OTPStatus.FAILED);
            }
            otpRepository.save(otp);
            log.warn("Invalid OTP code for contact: {}, attempts: {}", contact, otp.getAttemptCount());
            return false;
        }
    }

    public boolean isOTPVerified(String contact, OTPType type) {
        Optional<OTP> otpOptional = findLatestOTP(contact, type);
        return otpOptional.isPresent() &&
                otpOptional.get().getStatus() == OTPStatus.VERIFIED;
    }

    public Optional<OTP> findLatestOTP(String contact, OTPType type) {
        if (type == OTPType.EMAIL_VERIFICATION) {
            return otpRepository.findTopByEmailAndTypeOrderByCreatedAtDesc(contact, type);
        } else {
            return otpRepository.findTopByPhoneAndTypeOrderByCreatedAtDesc(contact, type);
        }
    }

    private OTP createOTP(String email, String phone, String otpCode, OTPType type) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(type.getValidityInSeconds());

        return OTP.builder()
                .email(email)
                .phone(phone)
                .otpCode(otpCode)
                .type(type)
                .status(OTPStatus.PENDING)
                .createdAt(now)
                .expiresAt(expiresAt)
                .attemptCount(0)
                .build();
    }

    private String generateOTP() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }
}