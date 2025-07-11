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
    private  final OTPService otpService;
    private final UserSessionService sessionService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponseDto login(UserLoginDto loginDto) {

        try {
            log.info("Starting login for phone: {}", loginDto.getPhone());

            boolean isValidOTP = otpService.verifyOTP(loginDto.getPhone(), loginDto.getOtp(), OTPType.LOGIN);
            log.info("OTP validation result: {}", isValidOTP);

            if (!isValidOTP) {
                throw new InvalidCredentialsException("Invalid or expired OTP");
            }

            User user = userRepository.findByPhone(loginDto.getPhone())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            log.info("User found: {}", user.getId());

            String jwt = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());

            if (user.getStatus() == UserStatus.SUSPENDED) {
                throw new InvalidCredentialsException("Account suspended");
            }

            UserSessionDto session = sessionService.createSession(
                    user.getId(),
                    loginDto.getDeviceId(),
                    loginDto.getDeviceType(),
                    loginDto.getFcmToken()
            );
            log.info("Session created with token: {}", session.getSessionToken());

            userService.updateLastLogin(user.getId());
            log.info("Last login updated");

            LoginResponseDto response = LoginResponseDto.builder()
                    .sessionToken(session.getSessionToken())
                    .refreshToken(session.getRefreshToken())
                    .accessToken(jwt)
                    .user(userService.getUserById(user.getId()))
                    .expiresIn(session.getRemainingTimeInSeconds())
                    .build();

            log.info("Login response built successfully");
            return response;

        } catch (Exception e) {
            log.error("Error during login: ", e);
            throw e;
        }
    }

}
