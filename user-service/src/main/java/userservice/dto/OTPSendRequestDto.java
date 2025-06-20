package userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import userservice.enums.OTPType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OTPSendRequestDto {

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number format")
    private String phone;

    @NotNull(message = "OTP type is required")
    private OTPType type;

    // Validation: Either email or phone must be provided based on OTP type
    public boolean isValid() {
        return switch (type) {
            case EMAIL_VERIFICATION, PASSWORD_RESET -> email != null && !email.trim().isEmpty();
            case PHONE_VERIFICATION, LOGIN -> phone != null && !phone.trim().isEmpty();
            case REGISTRATION -> (email != null && !email.trim().isEmpty()) ||
                    (phone != null && !phone.trim().isEmpty());
        };
    }
}

