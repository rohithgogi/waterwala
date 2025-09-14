package userservice.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import userservice.dto.SessionData;
import userservice.service.UserSessionService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final UserSessionService sessionService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return path.startsWith("/api/v1/auth/send-otp") ||
                path.startsWith("/api/v1/auth/login") ||
                path.equals("/api/v1/users/register") ||
                path.startsWith("/api/v1/users/exists/") ||
                path.startsWith("/api/v1/otp/") ||
                path.startsWith("/api/v1/sessions/create") ||
                path.startsWith("/api/v1/sessions/refresh") ||
                path.startsWith("/api/v1/sessions/validate") ||
                // Swagger endpoints
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars") ||
                path.equals("/swagger-ui.html");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.debug("Processing authentication for request: {} {}", request.getMethod(), request.getRequestURI());

        try {
            String jwt = getTokenFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                // First validate JWT structure and signature
                if (tokenProvider.validateToken(jwt)) {

                    // Then check session validity in Redis (fast lookup)
                    SessionData sessionData = sessionService.getSessionData(jwt);

                    if (sessionData != null && sessionData.isValid()) {
                        Long userId = sessionData.getUserId();
                        String role = sessionData.getUserRole();

                        log.debug("Authenticated user: {} with role: {} from Redis session", userId, role);

                        // Create authorities with ROLE_ prefix for Spring Security
                        List<SimpleGrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_" + role)
                        );

                        // Create authentication token with userId as principal
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userId, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // Update last accessed time in Redis (async operation)
                        sessionService.updateLastAccessed(jwt);

                        log.debug("Successfully authenticated user: {} via Redis session", userId);
                    } else {
                        log.warn("Session not found or invalid in Redis for token");
                        // Optional: Add token to blacklist if it's a valid JWT but invalid session
                    }
                } else {
                    log.debug("JWT token validation failed");
                }
            } else {
                log.debug("No JWT token found in request");
            }
        } catch (ExpiredJwtException ex) {
            log.error("JWT token is expired: {}", ex.getMessage());
            // Could add blacklisting logic here for expired tokens
            String expiredToken = getTokenFromRequest(request);
            if (StringUtils.hasText(expiredToken)) {
                // Optionally blacklist the expired token to prevent reuse
                log.debug("Could blacklist expired token: {}", expiredToken);
            }
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Cannot set user authentication: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}