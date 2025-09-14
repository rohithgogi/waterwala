package userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import userservice.dto.SessionData;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSessionService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String,String> stringRedisTemplate;

    private static final String SESSION_KEY_PREFIX = "session:";
    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh:";
    private static final String USER_SESSIONS_KEY_PREFIX = "user_sessions:";
    private static final String BLACKLIST_KEY_PREFIX = "blacklist:";

    public void storeSession(SessionData sessionData){
        try{
            String sessionKey=SESSION_KEY_PREFIX+sessionData.getSessionId();
            String refreshKey=REFRESH_TOKEN_KEY_PREFIX+sessionData.getRefreshToken();
            String userSessionsKey=USER_SESSIONS_KEY_PREFIX+sessionData.getUserId();

            // Calculate TTL
            long ttlSeconds = Duration.between(LocalDateTime.now(), sessionData.getExpiresAt()).getSeconds();

            // Store session data
            redisTemplate.opsForValue().set(sessionKey, sessionData, ttlSeconds, TimeUnit.SECONDS);

            // Store refresh token mapping
            redisTemplate.opsForValue().set(refreshKey, sessionData.getSessionToken(), ttlSeconds, TimeUnit.SECONDS);

            // Add to user's active sessions set
            redisTemplate.opsForSet().add(userSessionsKey, sessionData.getSessionToken());
            redisTemplate.expire(userSessionsKey, ttlSeconds, TimeUnit.SECONDS);

            log.info("Stored session in Redis: {}", sessionData.getSessionToken());
        } catch (Exception e) {
            log.error("Error storing session in Redis: {}", e.getMessage(), e);
        }
    }

    //retrieve session data by session token
    public SessionData getSession(String sessionToken){
        try{
            String sessionKey = SESSION_KEY_PREFIX + sessionToken;
            Object sessionObj = redisTemplate.opsForValue().get(sessionKey);

            if (sessionObj instanceof SessionData sessionData) {
                // Update last accessed time
                sessionData.setLastAccessedAt(LocalDateTime.now());
                updateLastAccessed(sessionToken, sessionData);
                return sessionData;
            }
            return null;
        } catch (Exception e) {
            log.error("Error retrieving session from Redis: {}", e.getMessage(), e);
            return null;
        }
    }

    public SessionData getSessionByRefreshToken(String refreshToken) {
        try {
            String refreshKey = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
            String sessionToken = (String) redisTemplate.opsForValue().get(refreshKey);

            if (sessionToken != null) {
                return getSession(sessionToken);
            }
            return null;
        } catch (Exception e) {
            log.error("Error retrieving session by refresh token from Redis: {}", e.getMessage(), e);
            return null;
        }
    }

    //specific session
    public void invalidateSession(String sessionToken) {
        try {
            SessionData sessionData = getSession(sessionToken);
            if (sessionData != null) {
                // Remove from Redis
                String sessionKey = SESSION_KEY_PREFIX + sessionToken;
                String refreshKey = REFRESH_TOKEN_KEY_PREFIX + sessionData.getRefreshToken();
                String userSessionsKey = USER_SESSIONS_KEY_PREFIX + sessionData.getUserId();

                redisTemplate.delete(sessionKey);
                redisTemplate.delete(refreshKey);
                redisTemplate.opsForSet().remove(userSessionsKey, sessionToken);

                // Add to blacklist
                blacklistToken(sessionToken, sessionData.getExpiresAt());

                log.info("Invalidated session: {}", sessionToken);
            }
        } catch (Exception e) {
            log.error("Error invalidating session: {}", e.getMessage(), e);
        }
    }

    public void invalidateAllUserSessions(Long userId) {
        try {
            String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;
            Set<Object> sessionTokens = redisTemplate.opsForSet().members(userSessionsKey);

            if (sessionTokens != null && !sessionTokens.isEmpty()) {
                for (Object tokenObj : sessionTokens) {
                    if (tokenObj instanceof String sessionToken) {
                        invalidateSession(sessionToken);
                    }
                }
                // Remove the user sessions set
                redisTemplate.delete(userSessionsKey);
                log.info("Invalidated all sessions for user: {}", userId);
            }
        } catch (Exception e) {
            log.error("Error invalidating all user sessions: {}", e.getMessage(), e);
        }
    }

    public void invalidateSessionsByDevice(Long userId, String deviceId) {
        try {
            String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;
            Set<Object> sessionTokens = redisTemplate.opsForSet().members(userSessionsKey);

            if (sessionTokens != null) {
                for (Object tokenObj : sessionTokens) {
                    if (tokenObj instanceof String sessionToken) {
                        SessionData sessionData = getSession(sessionToken);
                        if (sessionData != null && deviceId.equals(sessionData.getDeviceId())) {
                            invalidateSession(sessionToken);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error invalidating sessions by device: {}", e.getMessage(), e);
        }
    }

    public boolean isSessionValid(String sessionToken) {
        try {
            // Check if token is blacklisted first
            if (isTokenBlacklisted(sessionToken)) {
                return false;
            }

            SessionData sessionData = getSession(sessionToken);
            return sessionData != null && sessionData.isValid();
        } catch (Exception e) {
            log.error("Error checking session validity: {}", e.getMessage(), e);
            return false;
        }
    }

    public void updateSession(SessionData sessionData) {
        try {
            String sessionKey = SESSION_KEY_PREFIX + sessionData.getSessionToken();

            // Check if session exists
            if (redisTemplate.hasKey(sessionKey)) {
                long ttlSeconds = Duration.between(LocalDateTime.now(), sessionData.getExpiresAt()).getSeconds();
                redisTemplate.opsForValue().set(sessionKey, sessionData, ttlSeconds, TimeUnit.SECONDS);
                log.debug("Updated session in Redis: {}", sessionData.getSessionToken());
            }
        } catch (Exception e) {
            log.error("Error updating session in Redis: {}", e.getMessage(), e);
        }
    }
    public void updateLastAccessed(String sessionToken, SessionData sessionData) {
        try {
            sessionData.setLastAccessedAt(LocalDateTime.now());
            updateSession(sessionData);
        } catch (Exception e) {
            log.error("Error updating last accessed time: {}", e.getMessage(), e);
        }
    }

    public void blacklistToken(String token, LocalDateTime expiresAt) {
        try {
            String blacklistKey = BLACKLIST_KEY_PREFIX + token;
            long ttlSeconds = Duration.between(LocalDateTime.now(), expiresAt).getSeconds();

            if (ttlSeconds > 0) {
                stringRedisTemplate.opsForValue().set(blacklistKey, "blacklisted", ttlSeconds, TimeUnit.SECONDS);
                log.debug("Blacklisted token: {}", token);
            }
        } catch (Exception e) {
            log.error("Error blacklisting token: {}", e.getMessage(), e);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        try {
            String blacklistKey = BLACKLIST_KEY_PREFIX + token;
            return stringRedisTemplate.hasKey(blacklistKey);
        } catch (Exception e) {
            log.error("Error checking token blacklist status: {}", e.getMessage(), e);
            return false;
        }
    }

    public Long getActiveSessionCount(Long userId) {
        try {
            String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;
            return redisTemplate.opsForSet().size(userSessionsKey);
        } catch (Exception e) {
            log.error("Error getting active session count: {}", e.getMessage(), e);
            return 0L;
        }
    }

    public void refreshSession(String oldSessionToken, SessionData newSessionData) {
        try {
            // Remove old session
            invalidateSession(oldSessionToken);

            // Store new session
            storeSession(newSessionData);

            log.info("Refreshed session: {} -> {}", oldSessionToken, newSessionData.getSessionToken());
        } catch (Exception e) {
            log.error("Error refreshing session: {}", e.getMessage(), e);
        }
    }

}
