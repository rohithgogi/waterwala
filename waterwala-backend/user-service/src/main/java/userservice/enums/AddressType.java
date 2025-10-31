package userservice.enums;

import lombok.Getter;

@Getter
public enum AddressType {
    HOME("Home", "🏠"),
    OFFICE("Office", "🏢"),
    OTHER("Other", "📍");

    private final String displayName;
    private final String icon;

    AddressType(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }
}
