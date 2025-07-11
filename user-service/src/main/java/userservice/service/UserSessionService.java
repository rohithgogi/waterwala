package userservice.service;


import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
@Transactional
public class UserSessionService {
    private final UserSessionRepository sessionRepository;
    private final UserRepository userRepository;
    @Transactional
    public UserSessionDto createSession(Long userId, String deviceId, String deviceType, String fcmToken ){

        User user=userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User not found with ID: "+userId));
        //deactivating existing sessions for the same device
        sessionRepository.deactivateSessionsByUserIdAndDeviceId(userId,deviceId);

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

        return convertToDto(savedSession);
    }

    public UserSessionDto refreshSession(String refreshToken){
        UserSession session = sessionRepository.findByRefreshTokenAndIsActiveTrue(refreshToken)
                .orElseThrow(()-> new SessionNotFoundException("Invalid or Expired refresh token"));

        //check if refresh token is expired
        if(session.getExpiresAt().isBefore(LocalDateTime.now())){
            session.setIsActive(false);
            sessionRepository.save(session);
            throw new SessionNotFoundException("Refresh token expired");
        }

        //Generate new token
        session.setSessionToken(generateSessionToken());
        session.setRefreshToken(generateRefreshToken());
        session.setExpiresAt(LocalDateTime.now().plusDays(30));
        session.setLastAccessedAt(LocalDateTime.now());

        UserSession updatesSession=sessionRepository.save(session);
        return convertToDto(updatesSession);
    }

    public void updateLastAccessed(String sessionToken){
        sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken)
                .ifPresent(session->{
                    session.setLastAccessedAt(LocalDateTime.now());
                    sessionRepository.save(session);
                });
    }
    @PreAuthorize("hasRole('ADMIN') or hasAnyRole('CUSTOMER', 'BUSINESS_OWNER')")
    public void deactivateSession(String sessionToken){
        UserSession session = sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken)
                .orElseThrow(() -> new SessionNotFoundException("Session not found or already inactive"));

        session.setIsActive(false);
        sessionRepository.save(session);
    }
    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('BUSINESS_OWNER', 'CUSTOMER') and #userId == authentication.principal)")
    public void deactivateAllSessionsByUserId(Long userId){
        sessionRepository.deactivateAllSessionsByUserId(userId);

    }
    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('BUSINESS_OWNER', 'CUSTOMER') and #userId == authentication.principal)")
    public List<UserSessionDto> getUserActiveSessions(Long userId){
        List<UserSession> sessions=sessionRepository.findByUserIdAndIsActiveTrueOrderByLastAccessedAtDesc(userId);
        return sessions.stream()
                .map(this::convertToDto)
                .toList();
    }

    public boolean isSessionValid(String sessionToken){
        return sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken)
                .map(session->session.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElse(false);

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
