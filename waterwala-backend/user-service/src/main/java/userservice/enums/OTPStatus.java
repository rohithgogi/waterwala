package userservice.enums;

import lombok.Getter;

@Getter
public enum OTPStatus {
    PENDING("Pending", "OTP sent and waiting for verification"),
    VERIFIED("Verified", "OTP successfully verified"),
    EXPIRED("Expired", "OTP has expired"),
    FAILED("Failed", "OTP verification failed");

    private final String displayName;
    private final String description;

    OTPStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
