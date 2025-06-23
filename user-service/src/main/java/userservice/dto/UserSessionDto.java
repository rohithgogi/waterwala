package userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionDto {

    private Long id;
    private String sessionToken;
    private String refreshToken;
    private String deviceId;
    private String deviceType;
    private String fcmToken;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastAccessedAt;
    private Boolean isActive;

    // Computed fields for better UX
    public Boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public Long getRemainingTimeInSeconds() {
        if (expiresAt == null) return null;
        LocalDateTime now = LocalDateTime.now();
        if (expiresAt.isBefore(now)) return 0L;
        return java.time.Duration.between(now, expiresAt).getSeconds();
    }
}
