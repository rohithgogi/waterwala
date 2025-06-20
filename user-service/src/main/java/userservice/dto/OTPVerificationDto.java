package userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import userservice.enums.OTPType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OTPVerificationDto {

    @NotBlank(message = "Contact (email or phone) is required")
    private String contact; // Can be email or phone based on type

    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
    private String otpCode;

    @NotNull(message = "OTP type is required")
    private OTPType type;
}

