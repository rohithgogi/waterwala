package userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationDto {
    private Boolean exists;
    private Boolean isActive;
    private String role;
    private String email;
    private String message;
    private Long userId;
    private String firstName;
    private String lastName;
    private Boolean phoneVerified;
    private Boolean emailVerified;
    private String status;
}