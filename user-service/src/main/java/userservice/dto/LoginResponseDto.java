package userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String sessionToken;
    private String refreshToken;
    private String accessToken;
    private Long expiresIn;
    private UserResponseDto user;
}