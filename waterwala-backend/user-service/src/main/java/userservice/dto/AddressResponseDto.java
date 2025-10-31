package userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import userservice.enums.AddressType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto {

    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private AddressType type;
    private Boolean isDefault;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed field for display
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        address.append(addressLine1);

        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            address.append(", ").append(addressLine2);
        }

        if (landmark != null && !landmark.trim().isEmpty()) {
            address.append(", ").append(landmark);
        }

        address.append(", ").append(city);
        address.append(", ").append(state);
        address.append(" - ").append(pincode);

        if (country != null && !country.equals("India")) {
            address.append(", ").append(country);
        }

        return address.toString();
    }
}
