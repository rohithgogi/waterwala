package userservice.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import userservice.config.TwilioConfig;
import userservice.enums.OTPType;
import userservice.exceptions.SmsDeliveryException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    private final TwilioConfig twilioConfig;

    public void sendOTP(String toPhoneNumber, String otpCode, OTPType otpType) {
        try {
            String messageBody = buildOtpMessage(otpCode, otpType);

            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(twilioConfig.getPhoneNumber()),
                    messageBody
            ).create();

            log.info("SMS sent successfully. SID: {}, To: {}, Type: {}",
                    message.getSid(), toPhoneNumber, otpType);

        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toPhoneNumber, e.getMessage());
            throw new SmsDeliveryException("Failed to send SMS: " + e.getMessage());
        }
    }

    private String buildOtpMessage(String otpCode, OTPType otpType) {
        return switch (otpType) {
            case LOGIN -> String.format(
                    "Your WaterWala login OTP is: %s\n\nValid for 5 minutes.\nDo not share this code with anyone.",
                    otpCode
            );
            case PHONE_VERIFICATION -> String.format(
                    "Your WaterWala phone verification code is: %s\n\nValid for 10 minutes.",
                    otpCode
            );
            case PASSWORD_RESET -> String.format(
                    "Your WaterWala password reset OTP is: %s\n\nValid for 5 minutes.\nIf you didn't request this, please ignore.",
                    otpCode
            );
            case REGISTRATION -> String.format(
                    "Welcome to WaterWala! Your registration OTP is: %s\n\nValid for 10 minutes.",
                    otpCode
            );
            default -> String.format(
                    "Your WaterWala verification code is: %s",
                    otpCode
            );
        };
    }

    public void sendCustomMessage(String toPhoneNumber, String message) {
        try {
            Message twilioMessage = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(twilioConfig.getPhoneNumber()),
                    message
            ).create();

            log.info("Custom SMS sent successfully. SID: {}, To: {}",
                    twilioMessage.getSid(), toPhoneNumber);

        } catch (Exception e) {
            log.error("Failed to send custom SMS to {}: {}", toPhoneNumber, e.getMessage());
            throw new SmsDeliveryException("Failed to send SMS: " + e.getMessage());
        }
    }
}