package orderservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateDeliveryAddressRequest {
    @NotNull
    private String recipientName;

    @NotNull
    private String recipientPhone;

    private String recipientEmail;

    @NotNull
    private String addressLine1;

    private String addressLine2;

    @NotNull
    private String city;

    @NotNull
    private String state;

    @NotNull
    private String pincode;

    private String landmark;
    private Double latitude;
    private Double longitude;
}
