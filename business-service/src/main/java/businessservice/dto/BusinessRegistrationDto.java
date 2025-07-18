package businessservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessRegistrationDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Business name is required")
    @Size(max = 255, message = "Business name must not exceed 255 characters")
    private String businessName;

    @NotBlank(message = "Business type is required")
    @Size(max = 100, message = "Business type must not exceed 100 characters")
    private String businessType;

    @NotBlank(message = "Business registration number is required")
    @Pattern(regexp = "^[A-Z0-9]{10,20}$", message = "Invalid business registration number format")
    private String businessRegistrationNumber;

    @NotBlank(message = "GST number is required")
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$", message = "Invalid GST number format")
    private String gstNumber;

    @NotBlank(message = "Contact person name is required")
    @Size(max = 255, message = "Contact person name must not exceed 255 characters")
    private String contactPersonName;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;

    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number format")
    private String contactPhone;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private String logoUrl;

    @NotNull(message = "Address is required")
    @Valid
    private BusinessAddressDto address;

    @NotEmpty(message = "At least one service is required")
    @Valid
    private List<BusinessServiceDto> services;

    @NotEmpty(message = "Operating hours are required")
    @Valid
    private List<BusinessOperatingHoursDto> operatingHours;

}
