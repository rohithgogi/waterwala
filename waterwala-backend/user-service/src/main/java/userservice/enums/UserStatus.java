package userservice.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("Active", "User account is active"),
    INACTIVE("Inactive", "User account is temporarily inactive"),
    SUSPENDED("Suspended", "User account is suspended due to violation"),
    PENDING_VERIFICATION("Pending Verification", "User account needs verification");

    private final String displayName;
    private final String description;

    UserStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
