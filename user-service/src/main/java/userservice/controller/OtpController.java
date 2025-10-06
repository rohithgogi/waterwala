package userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userservice.dto.CommonResponseDto.StandardResponse;
import userservice.dto.OTPResponseDto;
import userservice.enums.OTPType;
import userservice.service.OTPService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("api/v1/otp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "OTP Management", description = "One-Time Password generation, sending, and verification APIs")
public class OtpController {

    private final OTPService otpService;

    @PostMapping("/send/email-verification")
    @Operation(summary = "Send email verification OTP", description = "Sends a 6-digit OTP to the specified email address for email verification purposes. OTP expires in 10 minutes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verification OTP sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email format"),
            @ApiResponse(responseCode = "429", description = "Too many OTP requests")
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<StandardResponse<OTPResponseDto>> sendEmailVerificationOTP(
            @Parameter(description = "Email address to send OTP") @RequestParam String email) {
        otpService.sendEmailVerificationOTP(email);
        OTPResponseDto response = OTPResponseDto.sent(
                OTPType.EMAIL_VERIFICATION,
                java.time.LocalDateTime.now().plusSeconds(600)
        );
        return ResponseEntity.ok(StandardResponse.success("Email verification OTP sent successfully", response));
    }

    @PostMapping("/send/phone-verification")
    @Operation(summary = "Send phone verification OTP", description = "Sends a 6-digit OTP to the specified phone number via SMS for phone verification. OTP expires in 10 minutes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Phone verification OTP sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid phone number format"),
            @ApiResponse(responseCode = "429", description = "Too many OTP requests")
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<StandardResponse<OTPResponseDto>> sendPhoneVerificationOTP(
            @Parameter(description = "Phone number in international format") @RequestParam String phone) {
        otpService.sendPhoneVerificationOTP(phone);
        OTPResponseDto response = OTPResponseDto.sent(
                OTPType.PHONE_VERIFICATION,
                java.time.LocalDateTime.now().plusSeconds(600)
        );
        return ResponseEntity.ok(StandardResponse.success("Phone Verification OTP sent successfully", response));
    }



    @PostMapping("/verify")
    @Operation(summary = "Verify OTP", description = "Verifies the provided OTP code for the specified contact and OTP type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
            @ApiResponse(responseCode = "400", description = "OTP verification failed")
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<StandardResponse<OTPResponseDto>> verifyOTP(
            @Parameter(description = "Contact (email or phone)") @RequestParam String contact,
            @Parameter(description = "6-digit OTP code") @RequestParam String otpCode,
            @Parameter(description = "OTP type") @RequestParam OTPType type) {
        boolean isValid = otpService.verifyOTP(contact, otpCode, type);
        if (isValid) {
            OTPResponseDto response = OTPResponseDto.verified(type);
            return ResponseEntity.ok(StandardResponse.success("OTP verified successfully", response));
        } else {
            OTPResponseDto response = OTPResponseDto.failure(type, "Invalid or expired OTP");
            return ResponseEntity.badRequest()
                    .body(StandardResponse.error("OTP verification failed", response));
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Check OTP verification status", description = "Checks if the OTP has been successfully verified for the given contact and type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No OTP record found")
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<StandardResponse<Boolean>> checkOtpStatus(
            @Parameter(description = "Contact (email or phone)") @RequestParam String contact,
            @Parameter(description = "OTP type") @RequestParam OTPType type) {
        boolean isVerified = otpService.isOTPVerified(contact, type);
        return ResponseEntity.ok(StandardResponse.success("OTP status retrieved", isVerified));
    }
}
