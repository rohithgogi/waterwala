package userservice.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    CUSTOMER("Customer"),
    BUSINESS_OWNER("Business Owner"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }
}


