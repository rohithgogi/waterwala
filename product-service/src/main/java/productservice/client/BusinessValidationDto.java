package productservice.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for business validation response from Business Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessValidationDto {

    @JsonProperty("exists")
    private Boolean exists;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("isVerified")
    private Boolean isVerified;

    @JsonProperty("businessId")
    private String businessId;

    @JsonProperty("businessName")
    private String businessName;

    @JsonProperty("ownerId")
    private Long ownerId;

    @JsonProperty("licenseNumber")
    private String licenseNumber;

    @JsonProperty("verificationStatus")
    private String verificationStatus;

    @JsonProperty("message")
    private String message;

    @JsonProperty("canCreateProducts")
    private Boolean canCreateProducts;

    @JsonProperty("businessType")
    private String businessType;
}