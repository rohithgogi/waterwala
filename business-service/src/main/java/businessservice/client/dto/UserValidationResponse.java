package businessservice.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationResponse {

    @JsonProperty("exists")
    private Boolean exists;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("role")
    private String role;

    @JsonProperty("email")
    private String email;

    @JsonProperty("message")
    private String message;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;


    @JsonProperty("phoneVerified")
    private Boolean phoneVerified;

    @JsonProperty("emailVerified")
    private Boolean emailVerified;

    @JsonProperty("status")
    private String status;
}
