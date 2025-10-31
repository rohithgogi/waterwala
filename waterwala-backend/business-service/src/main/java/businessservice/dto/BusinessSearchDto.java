package businessservice.dto;

import businessservice.model.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessSearchDto {

    private String pincode;
    private String city;
    private String state;
    private ServiceType serviceType;
    private String keyword;
    private Double latitude;
    private Double longitude;
    private Integer radius; // in kilometers
    private Double minRating;
    private String businessType;
}

