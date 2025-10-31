package userservice.dto;

import lombok.*;
import userservice.enums.OTPStatus;
import userservice.enums.OTPType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OTPResponseDto {

    private Boolean success;
    private String message;
    private OTPType type;
    private OTPStatus status;
    private LocalDateTime expiresAt;
    private Integer remainingAttempts;

    public static OTPResponseDto success(OTPType type, String message) {
        return OTPResponseDto.builder()
                .success(true)
                .type(type)
                .message(message)
                .build();
    }

    public static OTPResponseDto failure(OTPType type, String message) {
        return OTPResponseDto.builder()
                .success(false)
                .type(type)
                .message(message)
                .build();
    }

    public static OTPResponseDto sent(OTPType type, LocalDateTime expiresAt) {
        return OTPResponseDto.builder()
                .success(true)
                .type(type)
                .status(OTPStatus.PENDING)
                .message("OTP sent successfully")
                .expiresAt(expiresAt)
                .build();
    }

    public static OTPResponseDto verified(OTPType type) {
        return OTPResponseDto.builder()
                .success(true)
                .type(type)
                .status(OTPStatus.VERIFIED)
                .message("OTP verified successfully")
                .build();
    }

    public static OTPResponseDto expired(OTPType type) {
        return OTPResponseDto.builder()
                .success(false)
                .type(type)
                .status(OTPStatus.EXPIRED)
                .message("OTP has expired")
                .build();
    }

    public static OTPResponseDto failed(OTPType type, Integer remainingAttempts) {
        return OTPResponseDto.builder()
                .success(false)
                .type(type)
                .status(OTPStatus.FAILED)
                .message("Invalid OTP code")
                .remainingAttempts(remainingAttempts)
                .build();
    }
}