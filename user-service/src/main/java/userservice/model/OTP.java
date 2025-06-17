package userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import userservice.enums.OTPStatus;
import userservice.enums.OTPType;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OTP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phone;
    private String email;
    private String otpCode;

    @Enumerated(EnumType.STRING)
    private OTPType type;

    @Enumerated(EnumType.STRING)
    private OTPStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime verifiedAt;

    private Integer attemptCount = 0;
    private static final Integer MAX_ATTEMPTS = 3;
}
