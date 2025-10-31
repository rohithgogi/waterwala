package orderservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDeliveryAddressResponse {
    private Long id;
    private String recipientName;
    private String recipientPhone;
    private String recipientEmail;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String landmark;
    private Double latitude;
    private Double longitude;
    private orderservice.model.AddressType addressType;
}