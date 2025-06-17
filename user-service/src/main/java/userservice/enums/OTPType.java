package userservice.enums;

import lombok.Getter;

@Getter
public enum OTPType {
    LOGIN("Login OTP",300),
    REGISTRATION("Registration OTP", 600),
    PASSWORD_RESET("Password Reset OTP", 300),
    PHONE_VERIFICATION("Phone Verification OTP", 600),
    EMAIL_VERIFICATION("Email Verification OTP", 600);

    private final String displayName;
    private final int validityInSeconds;

    OTPType(String displayName, int validityInSeconds){
        this.displayName=displayName;
        this.validityInSeconds=validityInSeconds;
    }

}
