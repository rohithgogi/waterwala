package userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import userservice.model.UserSession;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession,Long> {
    // Find active session by session token (used in updateLastAccessed() and deactivateSession())
    Optional<UserSession> findBySessionTokenAndIsActiveTrue(String sessionToken);

    // Find active session by refresh token (used in refreshSession())
    Optional<UserSession> findByRefreshTokenAndIsActiveTrue(String refreshToken);

    // Get all active sessions for a user (used in getUserActiveSessions())
    List<UserSession> findByUserIdAndIsActiveTrueOrderByLastAccessedAtDesc(Long userId);

    // Deactivate sessions by user ID and device ID (used in createSession())
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.user.id = :userId AND s.deviceId = :deviceId")
    void deactivateSessionsByUserIdAndDeviceId(@Param("userId") Long userId, @Param("deviceId") String deviceId);

    // Deactivate all sessions for a user (used in deactivateAllUserSessions())
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.user.id = :userId")
    void deactivateAllSessionsByUserId(@Param("userId") Long userId);

    boolean existsByIdAndUserId(Long sessionId, Long userId);
}
