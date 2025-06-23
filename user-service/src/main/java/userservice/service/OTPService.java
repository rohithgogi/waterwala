package userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import userservice.enums.OTPStatus;
import userservice.enums.OTPType;
import userservice.model.OTP;
import userservice.repository.OTPRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OTPService {
    private final OTPRepository otpRepository;
    private final SecureRandom secureRandom=new SecureRandom();

    public void sendEmailVerificationOTP(String email){
        String otpCode=generateOTP();
        OTP otp=createOTP(email,null,otpCode, OTPType.EMAIL_VERIFICATION);
        otpRepository.save(otp);

    }

    public void sendPhoneVerificationOTP(String phone){
        String otpCode=generateOTP();
        OTP otp=createOTP(null,phone,otpCode,OTPType.PHONE_VERIFICATION);
        otpRepository.save(otp);
    }

    public void sendLoginOTP(String phone){
        String otpCode=generateOTP();
        OTP otp=createOTP(null,phone,otpCode,OTPType.LOGIN);
        otpRepository.save(otp);
    }

    public void passwordResetOTP(String phone){
        String otpCode=generateOTP();
        OTP otp=createOTP(null,phone,otpCode,OTPType.PASSWORD_RESET);
        otpRepository.save(otp);
    }

    public boolean verifyOTP(String contact, String otpCode, OTPType type){
        Optional<OTP> otpOptional=findLatestOTP(contact,type);

        if(otpOptional.isEmpty()){
            return false;
        }

        OTP otp=otpOptional.get();
        if(otp.getStatus()==OTPStatus.VERIFIED){
            return true;
        }

        if(otp.getExpiresAt().isBefore(LocalDateTime.now())){
            otp.setStatus(OTPStatus.EXPIRED);
            otpRepository.save(otp);
            return false;
        }

        if(otp.getAttemptCount()>=3){
            otp.setStatus(OTPStatus.FAILED);
            otpRepository.save(otp);
            return false;
        }
        otp.setAttemptCount(otp.getAttemptCount()+1);

        //verifying otp code
        if(otp.getOtpCode().equals(otpCode)){
            otp.setStatus(OTPStatus.VERIFIED);
            otp.setVerifiedAt(LocalDateTime.now());
            otpRepository.save(otp);
            return true;
        }else{
            if(otp.getAttemptCount()>=3){
                otp.setStatus(OTPStatus.FAILED);
            }
            otpRepository.save(otp);
            return false;
        }
    }

    public boolean isOTPVerified(String contact, OTPType type) {
        Optional<OTP> otpOptional = findLatestOTP(contact, type);
        return otpOptional.isPresent() &&
                otpOptional.get().getStatus() == OTPStatus.VERIFIED;
    }

    public Optional<OTP> findLatestOTP(String contact, OTPType type){
        if(type==OTPType.EMAIL_VERIFICATION || type==OTPType.PASSWORD_RESET){
            return otpRepository.findTopByEmailAndTypeOrderByCreatedAtDesc(contact, type);
        }else{
            return otpRepository.findTopByPhoneAndTypeOrderByCreatedAtDesc(contact, type);
        }
    }

    private OTP createOTP(String email, String phone, String otpCode, OTPType type) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(type.getValidityInSeconds());

        return OTP.builder()
                .email(email)
                .phone(phone)
                .otpCode(otpCode)
                .type(type)
                .status(OTPStatus.PENDING)
                .createdAt(now)
                .expiresAt(expiresAt)
                .attemptCount(0)
                .build();
    }
    private String generateOTP(){
        int otp=100000+secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }
}
