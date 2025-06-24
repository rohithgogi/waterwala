package userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userservice.dto.CommonResponseDto.ApiResponse;
import userservice.dto.UserRegistrationDto;
import userservice.dto.UserResponseDto;
import userservice.dto.UserUpdateDto;
import userservice.enums.UserRole;
import userservice.enums.UserStatus;
import userservice.service.UserService;

import java.sql.PreparedStatement;


@RestController
@RequestMapping("api/user-service/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins="*")
public class UserController {

    private final UserService service;
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto){
        UserResponseDto user= service.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully",user));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@RequestParam Long userId){
        UserResponseDto user=service.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully",user));
    }

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByEmail(@RequestParam String email){
        UserResponseDto user=service.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully",user));
    }

    @GetMapping("/{phone}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByPhone(@RequestParam String phone){
        UserResponseDto user=service.getUserByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully",user));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@PathVariable Long userId,@Valid @RequestBody UserUpdateDto updateDto){
        UserResponseDto user=service.updateUser(userId,updateDto);
        return ResponseEntity.ok(ApiResponse.success("User Updated Successfully",user));
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserStatus(@PathVariable Long userId, @RequestParam UserStatus status){
        service.updateUserStatus(userId,status);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", null));
    }

    @PatchMapping("/{userId}/verify-email")
    public ResponseEntity<ApiResponse<UserResponseDto>> verifyEmail(@PathVariable Long userId){
        service.verifyEmail(userId);
        return ResponseEntity.ok(ApiResponse.success("Email Verification completed"));
    }

    @PatchMapping("/{userId}/verify-phone")
    public ResponseEntity<ApiResponse<UserResponseDto>> verifyPhone(@PathVariable Long userId){
        service.verifyPhone(userId);
        return ResponseEntity.ok(ApiResponse.success("Phone Verification completed"));
    }

    @PatchMapping("/{userId}/last-login")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateLastLogin(@PathVariable Long userId){
        service.UpdateLastLogin(userId);
        return ResponseEntity.ok(ApiResponse.success("lAT login updated successfully"));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getAllUsers(@PageableDefault(size = 20) Pageable pageable){
        Page<UserResponseDto> users= service.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully",users));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getUsersByRole(@PathVariable UserRole role, @PageableDefault(size = 20) Pageable pageable){
        Page<UserResponseDto> users=service.getUsersByRole(role,pageable);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getUsersByStatus(@PathVariable UserStatus status, @PageableDefault(size = 20) Pageable pageable){
        Page<UserResponseDto> users=service.getUserByStatus(status,pageable);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(@PathVariable String email){
        boolean exists= service.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Email existence checked",exists));
    }

    @GetMapping("/exists/phone/{phone}")
    public ResponseEntity<ApiResponse<Boolean>> checkPhoneExists(@PathVariable String phone){
        boolean exists= service.existsByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success("Email existence checked",exists));
    }


}
