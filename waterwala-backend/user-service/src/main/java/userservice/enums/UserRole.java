package userservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum UserRole {
    CUSTOMER("Customer"),
    BUSINESS_OWNER("Business Owner"),
    ADMIN("Administrator");

    private final String displayName;
}


