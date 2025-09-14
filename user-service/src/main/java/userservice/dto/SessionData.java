package userservice.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionData {

    private Long sessionId;          // MySQL session ID for reference
    private Long userId;
    private String sessionToken;
    private String refreshToken;
    private String deviceId;
    private String deviceType;
    private String fcmToken;
    private String userRole;
    private String userEmail;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastAccessedAt;

    private Boolean isActive;

    // Helper methods
    public Boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public Boolean isValid() {
        return isActive && !isExpired();
    }

    public Long getRemainingTimeInSeconds() {
        if (expiresAt == null) return null;
        LocalDateTime now = LocalDateTime.now();
        if (expiresAt.isBefore(now)) return 0L;
        return java.time.Duration.between(now, expiresAt).getSeconds();
    }
}