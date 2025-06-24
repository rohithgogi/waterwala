package userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userservice.dto.CommonResponseDto.ApiResponse;
import userservice.dto.UserSessionDto;
import userservice.service.UserSessionService;

import java.util.List;

@RestController
@RequestMapping("api/user-service/v1/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserSessionController {
    private final UserSessionService sessionService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserSessionDto>> createSession(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            @RequestParam String deviceType,
            @RequestParam(required = false) String fcmToken){
        UserSessionDto session = sessionService.createSession(userId,deviceId,deviceType,fcmToken);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Session created successfully", session));
    }
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<UserSessionDto>> refreshSession(@RequestParam String refreshToken){
        UserSessionDto session= sessionService.refreshSession(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Session refreshed successfully",session));
    }

    @PatchMapping("/update-access")
    public ResponseEntity<ApiResponse<String>> updateLastAccessed(@RequestParam String sessionToken){
        sessionService.updateLastAccessed(sessionToken);
        return ResponseEntity.ok(ApiResponse.success("Updated last accessed time"));
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateSession(
            @RequestParam String sessionToken) {
        sessionService.deactivateSession(sessionToken);
        return ResponseEntity.ok(ApiResponse.success("Session deactivated successfully"));
    }

    @PatchMapping("/deactivate-all/{userId}")
    public ResponseEntity<ApiResponse<String>> deactivateAllSessions(@PathVariable Long userId) {
        sessionService.deactivateAllSessionsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("All sessions deactivated successfully"));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<ApiResponse<List<UserSessionDto>>> getUserActiveSessions(@PathVariable Long userId){
        List<UserSessionDto> sessions=sessionService.getUserActiveSessions(userId);
        return ResponseEntity.ok(ApiResponse.success("Retrieved active user sessions successfully"));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateSession(@RequestParam String sessionToken){
        boolean isValid=sessionService.isSessionValid(sessionToken);
        return ResponseEntity.ok(ApiResponse.success("Session validation completed", isValid));
    }
}
