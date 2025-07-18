package businessservice.dto;
import businessservice.model.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessOperatingHoursDto {

    private Long id;

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Open status is required")
    private Boolean isOpen;

    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean is24Hours;
}