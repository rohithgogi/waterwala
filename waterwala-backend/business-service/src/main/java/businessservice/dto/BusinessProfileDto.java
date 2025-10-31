package businessservice.dto;

import businessservice.model.BusinessStatus;
import businessservice.model.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProfileDto {

    private Long id;
    private Long userId;
    private String businessName;
    private String businessType;
    private String businessRegistrationNumber;
    private String gstNumber;
    private String contactPersonName;
    private String contactEmail;
    private String contactPhone;
    private String description;
    private BusinessStatus status;
    private VerificationStatus verificationStatus;
    private Boolean isActive;
    private Boolean isAvailable;
    private Double averageRating;
    private Integer totalReviews;
    private Integer totalOrders;
    private Integer completedOrders;
    private String logoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime verifiedAt;
    private List<BusinessAddressDto> addresses;
    private List<BusinessServiceDto> services;
    private List<BusinessOperatingHoursDto> operatingHours;
}