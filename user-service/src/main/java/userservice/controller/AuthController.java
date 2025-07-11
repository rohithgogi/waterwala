package userservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userservice.dto.CommonResponseDto.ApiResponse;
import userservice.dto.LoginResponseDto;
import userservice.dto.UserLoginDto;
import userservice.service.AuthService;
import userservice.service.OTPService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;
    private  final OTPService otpService;

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOTP(@RequestParam("phone") String phone){ //done
        otpService.sendLoginOTP(phone);
        return ResponseEntity.ok(ApiResponse.success("OTP sent to: "+phone));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody UserLoginDto loginDto){ //done
        LoginResponseDto response=authService.login(loginDto);
        return ResponseEntity.ok(ApiResponse.success("Login successful ", response));
    }
}
