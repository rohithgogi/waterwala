package userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.dto.SessionData;
import userservice.dto.UserSessionDto;
import userservice.enums.UserRole;
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
    private final RedisSessionService redisSessionService;

    @Transactional
    public UserSessionDto createSession(Long userId, String deviceId, String deviceType, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Deactivate existing sessions for the same device in both DB and Redis
        sessionRepository.deactivateSessionsByUserIdAndDeviceId(userId, deviceId);
        redisSessionService.invalidateSessionsByDevice(userId, deviceId);

        // Create new session
        UserSession session = UserSession.builder()
                .user(user)
                .sessionToken(generateSessionToken())
                .refreshToken(generateRefreshToken())
                .deviceId(deviceId)
                .deviceType(deviceType)
                .fcmToken(fcmToken)
                .expiresAt(LocalDateTime.now().plusDays(30)) // 30 days expiry
                .lastAccessedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        UserSession savedSession = sessionRepository.save(session);

        // Store in Redis for fast access
        SessionData sessionData = SessionData.builder()
                .sessionId(savedSession.getId())
                .userId(user.getId())
                .sessionToken(savedSession.getSessionToken())
                .refreshToken(savedSession.getRefreshToken())
                .deviceId(savedSession.getDeviceId())
                .deviceType(savedSession.getDeviceType())
                .fcmToken(savedSession.getFcmToken())
                .userRole(user.getRole().name())
                .userEmail(user.getEmail())
                .createdAt(savedSession.getCreatedAt())
                .expiresAt(savedSession.getExpiresAt())
                .lastAccessedAt(savedSession.getLastAccessedAt())
                .isActive(savedSession.getIsActive())
                .build();

        redisSessionService.storeSession(sessionData);

        log.info("Created new session for user: {} on device: {}", userId, deviceId);
        return convertToDto(savedSession);
    }

    public UserSessionDto refreshSession(String refreshToken) {
        // Check Redis first for fast lookup
        SessionData sessionData = redisSessionService.getSessionByRefreshToken(refreshToken);

        if (sessionData == null || !sessionData.isValid()) {
            // Fallback to database
            UserSession session = sessionRepository.findByRefreshTokenAndIsActiveTrue(refreshToken)
                    .orElseThrow(() -> new SessionNotFoundException("Invalid or expired refresh token"));

            sessionData = convertToSessionData(session);
        }

        // Check if refresh token is expired
        if (sessionData.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Invalidate expired session
            invalidateSessionInBothStores(sessionData.getSessionToken());
            throw new SessionNotFoundException("Refresh token expired");
        }

        // Generate new tokens
        String newSessionToken = generateSessionToken();
        String newRefreshToken = generateRefreshToken();
        LocalDateTime newExpiryTime = LocalDateTime.now().plusDays(30);

        // Update database
        UserSession session = sessionRepository.findById(sessionData.getSessionId())
                .orElseThrow(() -> new SessionNotFoundException("Session not found in database"));

        session.setSessionToken(newSessionToken);
        session.setRefreshToken(newRefreshToken);
        session.setExpiresAt(newExpiryTime);
        session.setLastAccessedAt(LocalDateTime.now());

        UserSession updatedSession = sessionRepository.save(session);

        // Update Redis - remove old and add new
        SessionData newSessionData = SessionData.builder()
                .sessionId(updatedSession.getId())
                .userId(sessionData.getUserId())
                .sessionToken(newSessionToken)
                .refreshToken(newRefreshToken)
                .deviceId(sessionData.getDeviceId())
                .deviceType(sessionData.getDeviceType())
                .fcmToken(sessionData.getFcmToken())
                .userRole(sessionData.getUserRole())
                .userEmail(sessionData.getUserEmail())
                .createdAt(sessionData.getCreatedAt())
                .expiresAt(newExpiryTime)
                .lastAccessedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        redisSessionService.refreshSession(sessionData.getSessionToken(), newSessionData);

        log.info("Refreshed session for user: {}", sessionData.getUserId());
        return convertToDto(updatedSession);
    }

    public void updateLastAccessed(String sessionToken) {
        // Update in Redis (fast)
        SessionData sessionData = redisSessionService.getSession(sessionToken);
        if (sessionData != null) {
            redisSessionService.updateLastAccessed(sessionToken, sessionData);

            // Asynchronously update database every few minutes to reduce DB load
            // For now, update immediately - can be optimized later with batch updates
            sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken)
                    .ifPresent(session -> {
                        session.setLastAccessedAt(LocalDateTime.now());
                        sessionRepository.save(session);
                    });
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasAnyRole('CUSTOMER', 'BUSINESS_OWNER')")
    public void deactivateSession(String sessionToken) {
        invalidateSessionInBothStores(sessionToken);
        log.info("Deactivated session: {}", sessionToken);
    }

    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('BUSINESS_OWNER', 'CUSTOMER') and #userId == authentication.principal)")
    public void deactivateAllSessionsByUserId(Long userId) {
        // Deactivate in database
        sessionRepository.deactivateAllSessionsByUserId(userId);

        // Deactivate in Redis
        redisSessionService.invalidateAllUserSessions(userId);

        log.info("Deactivated all sessions for user: {}", userId);
    }

    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('BUSINESS_OWNER', 'CUSTOMER') and #userId == authentication.principal)")
    public List<UserSessionDto> getUserActiveSessions(Long userId) {
        // Get from database for complete information
        List<UserSession> sessions = sessionRepository.findByUserIdAndIsActiveTrueOrderByLastAccessedAtDesc(userId);

        // Filter out sessions that are invalid in Redis
        return sessions.stream()
                .filter(session -> redisSessionService.isSessionValid(session.getSessionToken()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public boolean isSessionValid(String sessionToken) {
        // Check Redis first (fastest)
        boolean isValidInRedis = redisSessionService.isSessionValid(sessionToken);

        if (!isValidInRedis) {
            // Double-check database and sync if needed
            boolean isValidInDB = sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken)
                    .map(session -> session.getExpiresAt().isAfter(LocalDateTime.now()))
                    .orElse(false);

            if (isValidInDB) {
                // Sync to Redis if missing
                UserSession session = sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken).get();
                SessionData sessionData = convertToSessionData(session);
                redisSessionService.storeSession(sessionData);
                return true;
            }
        }

        return isValidInRedis;
    }

    /**
     * Get session data from Redis for JWT authentication
     */
    public SessionData getSessionData(String sessionToken) {
        SessionData sessionData = redisSessionService.getSession(sessionToken);

        if (sessionData == null) {
            // Fallback to database
            UserSession session = sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken)
                    .orElse(null);

            if (session != null) {
                sessionData = convertToSessionData(session);
                // Sync back to Redis
                redisSessionService.storeSession(sessionData);
            }
        }

        return sessionData;
    }

    /**
     * Invalidate session in both Redis and Database
     */
    private void invalidateSessionInBothStores(String sessionToken) {
        // Invalidate in Redis
        redisSessionService.invalidateSession(sessionToken);

        // Deactivate in database
        sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken)
                .ifPresent(session -> {
                    session.setIsActive(false);
                    sessionRepository.save(session);
                });
    }

    /**
     * Convert UserSession entity to SessionData for Redis
     */
    private SessionData convertToSessionData(UserSession session) {
        return SessionData.builder()
                .sessionId(session.getId())
                .userId(session.getUser().getId())
                .sessionToken(session.getSessionToken())
                .refreshToken(session.getRefreshToken())
                .deviceId(session.getDeviceId())
                .deviceType(session.getDeviceType())
                .fcmToken(session.getFcmToken())
                .userRole(session.getUser().getRole().name())
                .userEmail(session.getUser().getEmail())
                .createdAt(session.getCreatedAt())
                .expiresAt(session.getExpiresAt())
                .lastAccessedAt(session.getLastAccessedAt())
                .isActive(session.getIsActive())
                .build();
    }

    private String generateSessionToken() {
        return "sess_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String generateRefreshToken() {
        return "ref_" + UUID.randomUUID().toString().replace("-", "");
    }

    private UserSessionDto convertToDto(UserSession session) {
        return UserSessionDto.builder()
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
    }
}