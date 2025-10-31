
package userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import userservice.dto.CommonResponseDto.StandardResponse;
import userservice.dto.UserSessionDto;
import userservice.service.SecurityService;
import userservice.service.UserSessionService;

import java.util.List;

@RestController
@RequestMapping("api/v1/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Session Management", description = "JWT session management, refresh tokens, and device tracking APIs")
public class UserSessionController {
    private final UserSessionService sessionService;
    private final SecurityService securityService;

    @PostMapping("/create")
    @Operation(summary = "Create new session", description = "Creates a new user session with JWT tokens after successful authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Session created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid session creation parameters"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<StandardResponse<UserSessionDto>> createSession(
            @Parameter(description = "User ID", required = true) @RequestParam Long userId,
            @Parameter(description = "Unique device identifier", required = true) @RequestParam String deviceId,
            @Parameter(description = "Device type", required = true) @RequestParam String deviceType,
            @Parameter(description = "Firebase Cloud Messaging token for push notifications") @RequestParam(required = false) String fcmToken) {

        log.info("Creating session for user: {}, device: {}", userId, deviceId);
        UserSessionDto session = sessionService.createSession(userId, deviceId, deviceType, fcmToken);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("Session created successfully", session));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh session", description = "Refreshes an existing session using a valid refresh token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<StandardResponse<UserSessionDto>> refreshSession(
            @Parameter(description = "Valid refresh token", required = true) @RequestParam String refreshToken) {

        log.info("Refreshing session with refresh token");
        UserSessionDto session = sessionService.refreshSession(refreshToken);
        return ResponseEntity.ok(StandardResponse.success("Session refreshed successfully", session));
    }

    @PatchMapping("/update-access")
    @Operation(summary = "Update last accessed time", description = "Updates the last accessed timestamp for the session.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Last accessed time updated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid session token"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @PreAuthorize("@securityService.canModifyOwnSession(#sessionToken)")
    public ResponseEntity<StandardResponse<String>> updateLastAccessed(
            @Parameter(description = "Valid session token", required = true) @RequestParam String sessionToken) {

        log.info("Updating last accessed time for session");
        sessionService.updateLastAccessed(sessionToken);
        return ResponseEntity.ok(StandardResponse.success("Updated last accessed time"));
    }

    @PatchMapping("/deactivate")
    @Operation(summary = "Deactivate session", description = "Deactivates a specific session.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @PreAuthorize("@securityService.canModifyOwnSession(#sessionToken)")
    public ResponseEntity<StandardResponse<String>> deactivateSession(
            @Parameter(description = "Session token to deactivate", required = true) @RequestParam String sessionToken) {

        Long currentUserId = securityService.getCurrentUserId();
        log.info("User {} deactivating session", currentUserId);
        sessionService.deactivateSession(sessionToken);
        return ResponseEntity.ok(StandardResponse.success("Session deactivated successfully"));
    }

    @PatchMapping("/deactivate-all/{userId}")
    @Operation(summary = "Deactivate all user sessions", description = "Deactivates all active sessions for a user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All sessions deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("@securityService.canModifyUserData(#userId)")
    public ResponseEntity<StandardResponse<String>> deactivateAllSessions(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {

        Long currentUserId = securityService.getCurrentUserId();
        log.info("User {} deactivating all sessions for user {}", currentUserId, userId);
        sessionService.deactivateAllSessionsByUserId(userId);
        return ResponseEntity.ok(StandardResponse.success("All sessions deactivated successfully"));
    }

    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Get active user sessions", description = "Retrieves all active sessions for a specific user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active sessions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("@securityService.canAccessUserData(#userId)")
    public ResponseEntity<StandardResponse<List<UserSessionDto>>> getUserActiveSessions(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {

        Long currentUserId = securityService.getCurrentUserId();
        log.info("User {} accessing active sessions for user {}", currentUserId, userId);
        List<UserSessionDto> sessions = sessionService.getUserActiveSessions(userId);
        return ResponseEntity.ok(StandardResponse.success("Retrieved active user sessions successfully", sessions));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate session", description = "Validates if a session token is still valid and active")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session validation completed")
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<StandardResponse<Boolean>> validateSession(
            @Parameter(description = "Session token to validate", required = true) @RequestParam String sessionToken) {

        log.debug("Validating session token");
        boolean isValid = sessionService.isSessionValid(sessionToken);
        return ResponseEntity.ok(StandardResponse.success("Session validation completed", isValid));
    }
}