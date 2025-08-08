package userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import userservice.dto.CommonResponseDto.StandardResponse;
import userservice.dto.UserRegistrationDto;
import userservice.dto.UserResponseDto;
import userservice.dto.UserUpdateDto;
import userservice.enums.UserRole;
import userservice.enums.UserStatus;
import userservice.service.UserService;


@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "User registration, profile management, and user operations")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService service;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user account. This endpoint doesn't require authentication.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or user already exists"),
            @ApiResponse(responseCode = "409", description = "Email or phone number already registered")
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<StandardResponse<UserResponseDto>> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        UserResponseDto user = service.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("User registered successfully", user));
    }

    @GetMapping("/id/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieves user information by user ID")
    @PreAuthorize("@securityService.canAccessUserData(#userId)")
    public ResponseEntity<StandardResponse<UserResponseDto>> getUserById(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        UserResponseDto user = service.getUserById(userId);
        return ResponseEntity.ok(StandardResponse.success("User retrieved successfully", user));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieves user information by email address")
    @PreAuthorize("@securityService.isBusinessOwnerOrAdmin()")
    public ResponseEntity<StandardResponse<UserResponseDto>> getUserByEmail(
            @Parameter(description = "User email") @PathVariable String email) {
        UserResponseDto user = service.getUserByEmail(email);
        return ResponseEntity.ok(StandardResponse.success("User retrieved successfully", user));
    }

    @GetMapping("/phone/{phone}")
    @Operation(summary = "Get user by phone", description = "Retrieves user information by phone number")
    @PreAuthorize("@securityService.isBusinessOwnerOrAdmin()")
    public ResponseEntity<StandardResponse<UserResponseDto>> getUserByPhone(
            @Parameter(description = "Phone number") @PathVariable String phone) {
        UserResponseDto user = service.getUserByPhone(phone);
        return ResponseEntity.ok(StandardResponse.success("User retrieved successfully", user));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user", description = "Updates user profile information")
    @PreAuthorize("@securityService.canModifyUserData(#userId)")
    public ResponseEntity<StandardResponse<UserResponseDto>> updateUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Valid @RequestBody UserUpdateDto updateDto) {
        UserResponseDto user = service.updateUser(userId, updateDto);
        return ResponseEntity.ok(StandardResponse.success("User Updated Successfully", user));
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "Update user status", description = "Updates the status of a user (ACTIVE, INACTIVE, SUSPENDED)")
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseEntity<StandardResponse<UserResponseDto>> updateUserStatus(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "New status") @RequestParam UserStatus status) {
        service.updateUserStatus(userId, status);
        return ResponseEntity.ok(StandardResponse.success("User status updated successfully", null));
    }

    @PatchMapping("/{userId}/verify-email")
    @Operation(summary = "Verify email", description = "Marks user's email as verified")
    @SecurityRequirement(name = "")
    @PreAuthorize("@securityService.canAccessUserData(#userId)")
    public ResponseEntity<StandardResponse<UserResponseDto>> verifyEmail(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        service.verifyEmail(userId);
        return ResponseEntity.ok(StandardResponse.success("Email Verification completed"));
    }

    @PatchMapping("/{userId}/verify-phone")
    @Operation(summary = "Verify phone", description = "Marks user's phone number as verified")
    @SecurityRequirement(name = "")
    @PreAuthorize("@securityService.canAccessUserData(#userId)")
    public ResponseEntity<StandardResponse<UserResponseDto>> verifyPhone(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        service.verifyPhone(userId);
        return ResponseEntity.ok(StandardResponse.success("Phone Verification completed"));
    }

    @PatchMapping("/{userId}/last-login")
    @Operation(summary = "Update last login", description = "Updates the user's last login timestamp")
    @PreAuthorize("@securityService.canAccessUserData(#userId)")
    public ResponseEntity<StandardResponse<UserResponseDto>> updateLastLogin(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        service.updateLastLogin(userId);
        return ResponseEntity.ok(StandardResponse.success("Last login updated successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves paginated list of all users. Requires admin privileges.")
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseEntity<StandardResponse<Page<UserResponseDto>>> getAllUsers(@PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponseDto> users = service.getAllUsers(pageable);
        return ResponseEntity.ok(StandardResponse.success("Users retrieved successfully", users));
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieves users filtered by role")
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseEntity<StandardResponse<Page<UserResponseDto>>> getUsersByRole(
            @Parameter(description = "User role") @PathVariable UserRole role,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponseDto> users = service.getUsersByRole(role, pageable);
        return ResponseEntity.ok(StandardResponse.success("Users retrieved successfully", users));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get users by status", description = "Retrieves users filtered by status")
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseEntity<StandardResponse<Page<UserResponseDto>>> getUsersByStatus(
            @Parameter(description = "User status") @PathVariable UserStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponseDto> users = service.getUserByStatus(status, pageable);
        return ResponseEntity.ok(StandardResponse.success("Users retrieved successfully", users));
    }

    @GetMapping("/exists/email/{email}")
    @Operation(summary = "Check email existence", description = "Checks if an email is already registered")
    @SecurityRequirement(name = "")
    @PreAuthorize("@securityService.isBusinessOwnerOrAdmin()")
    public ResponseEntity<StandardResponse<Boolean>> checkEmailExists(
            @Parameter(description = "Email to check") @PathVariable String email) {
        boolean exists = service.existsByEmail(email);
        return ResponseEntity.ok(StandardResponse.success("Email existence checked", exists));
    }

    @GetMapping("/exists/phone/{phone}")
    @Operation(summary = "Check phone existence", description = "Checks if a phone number is already registered")
    @SecurityRequirement(name = "")
    @PreAuthorize("@securityService.isBusinessOwnerOrAdmin()")
    public ResponseEntity<StandardResponse<Boolean>> checkPhoneExists(
            @Parameter(description = "Phone number to check") @PathVariable String phone) {
        boolean exists = service.existsByPhone(phone);
        return ResponseEntity.ok(StandardResponse.success("Phone existence checked", exists));
    }
}