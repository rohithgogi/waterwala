package userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.dto.LoginResponseDto;
import userservice.dto.UserLoginDto;
import userservice.dto.UserSessionDto;
import userservice.enums.OTPType;
import userservice.enums.UserStatus;
import userservice.exceptions.InvalidCredentialsException;
import userservice.exceptions.UserNotFoundException;
import userservice.model.User;
import userservice.repository.UserRepository;
import userservice.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final OTPService otpService;
    private final UserSessionService sessionService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponseDto login(UserLoginDto loginDto) {

        try {
            log.info("Starting login for phone: {}", loginDto.getPhone());

            // Verify OTP
            boolean isValidOTP = otpService.verifyOTP(loginDto.getPhone(), loginDto.getOtp(), OTPType.LOGIN);
            log.info("OTP validation result: {}", isValidOTP);

            if (!isValidOTP) {
                throw new InvalidCredentialsException("Invalid or expired OTP");
            }

            // Find user
            User user = userRepository.findByPhone(loginDto.getPhone())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            log.info("User found: {}", user.getId());

            // Check user status
            if (user.getStatus() == UserStatus.SUSPENDED) {
                throw new InvalidCredentialsException("Account suspended");
            }

            // Generate JWT token
            String jwt = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());

            // Create session (MySQL only)
            UserSessionDto session = sessionService.createSession(
                    user.getId(),
                    loginDto.getDeviceId(),
                    loginDto.getDeviceType(),
                    loginDto.getFcmToken()
            );
            log.info("Session created with token: {}", session.getSessionToken());

            // Update last login
            userService.updateLastLogin(user.getId());
            log.info("Last login updated");

            // Build response
            LoginResponseDto response = LoginResponseDto.builder()
                    .sessionToken(session.getSessionToken())
                    .refreshToken(session.getRefreshToken())
                    .accessToken(jwt)
                    .user(userService.getUserById(user.getId()))
                    .expiresIn(session.getRemainingTimeInSeconds())
                    .build();

            log.info("Login successful for user: {}", user.getId());
            return response;

        } catch (Exception e) {
            log.error("Error during login: ", e);
            throw e;
        }
    }

    /**
     * Logout user by invalidating session (MySQL only)
     */
    public void logout(String sessionToken) {
        try {
            log.info("Starting logout for session: {}", sessionToken);

            // Invalidate session in MySQL
            sessionService.deactivateSession(sessionToken);

            log.info("Logout successful for session: {}", sessionToken);
        } catch (Exception e) {
            log.error("Error during logout: ", e);
            throw new InvalidCredentialsException("Logout failed");
        }
    }

    /**
     * Logout user from all devices (MySQL only)
     */
    public void logoutFromAllDevices(Long userId) {
        try {
            log.info("Starting logout from all devices for user: {}", userId);

            // Invalidate all sessions for the user
            sessionService.deactivateAllSessionsByUserId(userId);

            log.info("Logout from all devices successful for user: {}", userId);
        } catch (Exception e) {
            log.error("Error during logout from all devices: ", e);
            throw new InvalidCredentialsException("Logout from all devices failed");
        }
    }
}
