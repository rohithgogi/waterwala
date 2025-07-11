package userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userservice.dto.CommonResponseDto.ApiResponse;
import userservice.dto.OTPResponseDto;
import userservice.enums.OTPType;
import userservice.service.OTPService;

@RestController
@RequestMapping("api/v1/otp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OtpController {
    private final OTPService otpService;

    @PostMapping("/send/email-verification")
    public ResponseEntity<ApiResponse<OTPResponseDto>> sendEmailVerificationOTP(@RequestParam String email){ //done
        otpService.sendEmailVerificationOTP(email);
        OTPResponseDto response=OTPResponseDto.sent(OTPType.EMAIL_VERIFICATION,java.time.LocalDateTime.now().plusSeconds(600));
        return ResponseEntity.ok(ApiResponse.success("Email verification OTP sent successfully", response));
    }

    @PostMapping("/send/phone-verification")
    public ResponseEntity<ApiResponse<OTPResponseDto>> sendPhoneVerificationOTP(@RequestParam String phone){ //done
        otpService.sendPhoneVerificationOTP(phone);
        OTPResponseDto response=OTPResponseDto.sent(OTPType.PHONE_VERIFICATION,java.time.LocalDateTime.now().plusSeconds(600));
        return ResponseEntity.ok(ApiResponse.success("Phone Verification OTP sent successfully",response));
    }


    @PostMapping("/send/password-reset")
    public ResponseEntity<ApiResponse<OTPResponseDto>> sendPasswordResetOTP(@RequestParam String phone){
        otpService.passwordResetOTP(phone);
        OTPResponseDto response=OTPResponseDto.sent(OTPType.PASSWORD_RESET,java.time.LocalDateTime.now().plusSeconds(300));
        return ResponseEntity.ok(ApiResponse.success("Password Reset OTP sent successfully",response));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<OTPResponseDto>> verifyOTP(@RequestParam String contact, @RequestParam String otpCode, @RequestParam OTPType type){
       boolean isValid= otpService.verifyOTP(contact,otpCode,type);
       if(isValid){
           OTPResponseDto response=OTPResponseDto.verified(type);
           return ResponseEntity.ok(ApiResponse.success("OTP verified successfully",response));
       }else{
           OTPResponseDto response = OTPResponseDto.failure(type, "Invalid or expired OTP");
           return ResponseEntity.badRequest()
                   .body(ApiResponse.error("OTP verification failed",response));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> checkOtpStatus(@RequestParam String contact,@RequestParam OTPType type){
        boolean isVerified=otpService.isOTPVerified(contact,type);
        return ResponseEntity.ok(ApiResponse.success("OTP status retrieved",isVerified));
    }
}
