package businessservice.dto;
import businessservice.model.VerificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationDto {

    @NotNull(message = "Verification status is required")
    private VerificationStatus status;

    private String comments;
    private String rejectionReason;
}
