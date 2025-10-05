package userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.dto.UserSessionDto;
import userservice.exceptions.SessionNotFoundException;
import userservice.exceptions.UserNotFoundException;
import userservice.model.User;
import userservice.model.UserSession;
import userservice.repository.UserRepository;
import userservice.repository.UserSessionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserSessionService {
    private final UserSessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserSessionDto createSession(Long userId, String deviceId, String deviceType, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Deactivate existing sessions for the same device
        sessionRepository.deactivateSessionsByUserIdAndDeviceId(userId, deviceId);

        // Create new session for device tracking
        UserSession session = UserSession.builder()
                .user(user)
                .sessionToken(generateSessionToken())  // Keep unique session token
                .refreshToken(generateRefreshToken())
                .deviceId(deviceId)
                .deviceType(deviceType)
                .fcmToken(fcmToken)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .lastAccessedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        UserSession savedSession = sessionRepository.save(session);
        log.info("Created new session for user: {} on device: {}", userId, deviceId);
        return convertToDto(savedSession);
    }


    public UserSessionDto refreshSession(String refreshToken) {
        UserSession session = sessionRepository.findByRefreshTokenAndIsActiveTrue(refreshToken)
                .orElseThrow(() -> new SessionNotFoundException("Invalid or expired refresh token"));

        // Check if refresh token is expired
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Invalidate expired session
            session.setIsActive(false);
            sessionRepository.save(session);
            throw new SessionNotFoundException("Refresh token expired");
        }

        // Generate new tokens
        String newSessionToken = generateSessionToken();
        String newRefreshToken = generateRefreshToken();
        LocalDateTime newExpiryTime = LocalDateTime.now().plusDays(30);

        // Update session
        session.setSessionToken(newSessionToken);
        session.setRefreshToken(newRefreshToken);
        session.setExpiresAt(newExpiryTime);
        session.setLastAccessedAt(LocalDateTime.now());

        UserSession updatedSession = sessionRepository.save(session);
        log.info("Refreshed session for user: {}", session.getUser().getId());
        return convertToDto(updatedSession);
    }

    public void updateLastAccessed(String sessionToken) {
        sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken)
                .ifPresent(session -> {
                    session.setLastAccessedAt(LocalDateTime.now());
                    sessionRepository.save(session);
                });
    }

    @PreAuthorize("hasRole('ADMIN') or hasAnyRole('CUSTOMER', 'BUSINESS_OWNER')")
    public void deactivateSession(String sessionToken) {
        sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken)
                .ifPresent(session -> {
                    session.setIsActive(false);
                    sessionRepository.save(session);
                    log.info("Deactivated session: {}", sessionToken);
                });
    }

    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('BUSINESS_OWNER', 'CUSTOMER') and #userId == authentication.principal)")
    public void deactivateAllSessionsByUserId(Long userId) {
        sessionRepository.deactivateAllSessionsByUserId(userId);
        log.info("Deactivated all sessions for user: {}", userId);
    }

    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('BUSINESS_OWNER', 'CUSTOMER') and #userId == authentication.principal)")
    public List<UserSessionDto> getUserActiveSessions(Long userId) {
        List<UserSession> sessions = sessionRepository.findByUserIdAndIsActiveTrueOrderByLastAccessedAtDesc(userId);
        return sessions.stream()
                .filter(session -> session.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public boolean isSessionValid(String sessionToken) {
        return sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken)
                .map(session -> session.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    private String generateSessionToken() {
        return "sess_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String generateRefreshToken() {
        return "ref_" + UUID.randomUUID().toString().replace("-", "");
    }
    private UserSessionDto convertToDto(UserSession session) {
        UserSessionDto dto = UserSessionDto.builder()
                .id(session.getId())
                .sessionToken(session.getSessionToken())
                .refreshToken(session.getRefreshToken())
                .deviceId(session.getDeviceId())
                .deviceType(session.getDeviceType())
                .fcmToken(session.getFcmToken())
                .createdAt(session.getCreatedAt())
                .expiresAt(session.getExpiresAt())
                .lastAccessedAt(session.getLastAccessedAt())
                .isActive(session.getIsActive())
                .build();

        return dto;
    }

}