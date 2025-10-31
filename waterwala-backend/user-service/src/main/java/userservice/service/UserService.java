package userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import userservice.dto.UserRegistrationDto;
import userservice.dto.UserResponseDto;
import userservice.dto.UserUpdateDto;
import userservice.dto.UserValidationDto;
import userservice.enums.UserRole;
import userservice.enums.UserStatus;
import userservice.exceptions.UserAlreadyExistsException;
import userservice.exceptions.UserNotFoundException;
import userservice.model.User;
import userservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OTPService otpService;

    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("User with email : " + registrationDto.getEmail() + " already exists.");
        }
        if (userRepository.existsByPhone(registrationDto.getPhone())) {
            throw new UserAlreadyExistsException("User with phone number: " + registrationDto.getPhone() + " already exists.");
        }

        User user = User.builder()
                .email(registrationDto.getEmail())
                .phone(registrationDto.getPhone())
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .role(registrationDto.getRole() != null ? registrationDto.getRole() : UserRole.CUSTOMER)
                .status(UserStatus.PENDING_VERIFICATION)
                .emailVerified(false)
                .phoneVerified(false)
                .build();

        User savedUser = userRepository.save(user);


        return convertToResponseDto(savedUser);

    }
    public UserValidationDto validateUser(Long userId){
        Optional<User> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            return UserValidationDto.builder()
                    .exists(false)
                    .isActive(false)
                    .message("User not found")
                    .userId(userId)
                    .phoneVerified(false)
                    .emailVerified(false)
                    .status("NOT_FOUND")
                    .build();
        }

        User user = userOpt.get();
        boolean isActive = user.getStatus() == UserStatus.ACTIVE;

        return UserValidationDto.builder()
                .exists(true)
                .isActive(isActive)
                .role(user.getRole().name())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userId(user.getId())
                .phoneVerified(user.getPhoneVerified())
                .emailVerified(user.getEmailVerified())
                .status(user.getStatus().name())
                .message(isActive ? "User validation successful" : "User is not active - current status: " + user.getStatus())
                .build();
    }
    public UserResponseDto getUserById(Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User not found with ID: " + userId));
        return convertToResponseDto(user);
    }
    public UserResponseDto getUserByEmail(String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UserNotFoundException("User not found with email: " + email));
        return convertToResponseDto(user);
    }
    public UserResponseDto getUserByPhone(String phone){
        User user=userRepository.findByPhone(phone)
                .orElseThrow(()->new UserNotFoundException("User not found with phone: " + phone));
        return convertToResponseDto(user);
    }
    public UserResponseDto updateUser(Long userId, UserUpdateDto updateDto){

        User user=userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User not found with ID: "+userId));

        if(updateDto.getFirstName()!=null){
            user.setFirstName(updateDto.getFirstName());
        }
        if(updateDto.getLastName()!=null){
            user.setLastName(updateDto.getLastName());
        }
        if(updateDto.getProfileImageURL()!=null){
            user.setProfileImageURL(updateDto.getProfileImageURL());
        }

        User updatedUser=userRepository.save(user);
        return convertToResponseDto(updatedUser);

    }
    public void updateUserStatus(Long userId, UserStatus userStatus){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User not found with ID: "+userId));

        user.setStatus(userStatus);
        userRepository.save(user);
    }
    public void verifyEmail(Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User not found with ID: "+userId));
        user.setEmailVerified(true);

        if(user.getPhoneVerified()){
            user.setStatus(UserStatus.ACTIVE);
        }

        userRepository.save(user);
    }
    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('CUSTOMER', 'BUSINESS_OWNER') and #userId == authentication.principal)")
    public void verifyPhone(Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User not found with ID: "+userId));
        user.setPhoneVerified(true);

        if(user.getEmailVerified()){
            user.setStatus(UserStatus.ACTIVE);
        }

        userRepository.save(user);
    }

    public void updateLastLogin(Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User not found with ID: "+userId));
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToResponseDto);
    }

    public Page<UserResponseDto> getUsersByRole(UserRole role,Pageable pageable){
        return userRepository.findByRole(role,pageable)
                .map(this::convertToResponseDto);
    }
    public Page<UserResponseDto> getUserByStatus(UserStatus status, Pageable pageable){
        return userRepository.findByStatus(status,pageable)
                .map(this::convertToResponseDto);
    }
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    private UserResponseDto convertToResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .status(user.getStatus())
                .profileImageURL(user.getProfileImageURL())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
