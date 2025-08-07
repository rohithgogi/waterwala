package userservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userservice.dto.CommonResponseDto.StandardResponse;
import userservice.dto.LoginResponseDto;
import userservice.dto.UserLoginDto;
import userservice.service.AuthService;
import userservice.service.OTPService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "User authentication and authorization APIs")

public class AuthController {
    private final AuthService authService;
    private  final OTPService otpService;

    @PostMapping("/send-otp")
    @Operation(
            summary = "Send login OTP",
            description = "Sends a one-time password to the specified phone number for login purposes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid phone number format"),
            @ApiResponse(responseCode = "429", description = "Too many requests - rate limit exceeded")
    })
    public ResponseEntity<StandardResponse<String>> sendOTP(@Parameter(description = "Phone number in international format", required = true)
                                                                @RequestParam("phone") String phone){ //done
        otpService.sendLoginOTP(phone);
        return ResponseEntity.ok(StandardResponse.success("OTP sent to: "+phone));
    }

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticates user with phone number and OTP, returns JWT tokens"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or OTP"),
            @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    public ResponseEntity<StandardResponse<LoginResponseDto>> login(@Valid @RequestBody UserLoginDto loginDto){ //done
        LoginResponseDto response=authService.login(loginDto);
        return ResponseEntity.ok(StandardResponse.success("Login successful ", response));
    }
}
