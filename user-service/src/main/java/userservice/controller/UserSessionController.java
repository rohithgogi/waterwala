package userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userservice.dto.CommonResponseDto.StandardResponse;
import userservice.dto.UserSessionDto;
import userservice.service.UserSessionService;

import java.util.List;

@RestController
@RequestMapping("api/v1/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserSessionController {
    private final UserSessionService sessionService;

    @PostMapping("/create")
    public ResponseEntity<StandardResponse<UserSessionDto>> createSession(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            @RequestParam String deviceType,
            @RequestParam(required = false) String fcmToken){
        UserSessionDto session = sessionService.createSession(userId,deviceId,deviceType,fcmToken);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("Session created successfully", session));
    }
    @PostMapping("/refresh")
    public ResponseEntity<StandardResponse<UserSessionDto>> refreshSession(@RequestParam String refreshToken){
        UserSessionDto session= sessionService.refreshSession(refreshToken);
        return ResponseEntity.ok(StandardResponse.success("Session refreshed successfully",session));
    }

    @PatchMapping("/update-access")
    public ResponseEntity<StandardResponse<String>> updateLastAccessed(@RequestParam String sessionToken){
        sessionService.updateLastAccessed(sessionToken);
        return ResponseEntity.ok(StandardResponse.success("Updated last accessed time"));
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<StandardResponse<String>> deactivateSession(
            @RequestParam String sessionToken) {
        sessionService.deactivateSession(sessionToken);
        return ResponseEntity.ok(StandardResponse.success("Session deactivated successfully"));
    }

    @PatchMapping("/deactivate-all/{userId}")
    public ResponseEntity<StandardResponse<String>> deactivateAllSessions(@PathVariable Long userId) {
        sessionService.deactivateAllSessionsByUserId(userId);
        return ResponseEntity.ok(StandardResponse.success("All sessions deactivated successfully"));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<StandardResponse<List<UserSessionDto>>> getUserActiveSessions(@PathVariable Long userId){
        List<UserSessionDto> sessions=sessionService.getUserActiveSessions(userId);
        return ResponseEntity.ok(StandardResponse.success("Retrieved active user sessions successfully"));
    }

    @GetMapping("/validate")
    public ResponseEntity<StandardResponse<Boolean>> validateSession(@RequestParam String sessionToken){
        boolean isValid=sessionService.isSessionValid(sessionToken);
        return ResponseEntity.ok(StandardResponse.success("Session validation completed", isValid));
    }
}
