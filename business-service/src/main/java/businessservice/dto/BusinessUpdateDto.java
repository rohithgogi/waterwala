package businessservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessUpdateDto {

    @Size(max = 255, message = "Business name must not exceed 255 characters")
    private String businessName;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 255, message = "Contact person name must not exceed 255 characters")
    private String contactPersonName;

    @Email(message = "Invalid email format")
    private String contactEmail;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number format")
    private String contactPhone;

    private String logoUrl;
    private Boolean isAvailable;

}