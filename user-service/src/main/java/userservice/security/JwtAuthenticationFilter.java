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

        // Skip JWT validation for public endpoints
        return path.startsWith("/api/v1/auth/") ||
                path.startsWith("/api/v1/users/register") ||
                path.startsWith("/api/v1/users/exists/") ||
                path.startsWith("/api/v1/otp/") ||  // This is the key addition
                path.startsWith("/api/v1/sessions/create") ||
                path.startsWith("/api/v1/sessions/refresh") ||
                path.matches("/api/v1/users/\\d+/verify-phone") ||
                path.matches("/api/v1/users/\\d+/verify-email");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Add this log
        String header = request.getHeader("Authorization");
        log.info("Authorization Header: {}", header);

        try {
            String jwt = getTokenFromRequest(request);
            log.info("Extracted Token: {}", jwt); // Add this line too

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                if (sessionService.isSessionValid(jwt)) {
                    Long userId = tokenProvider.getUserIdFromToken(jwt);
                    String role = tokenProvider.getRoleFromToken(jwt);

                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Update last accessed time
                    sessionService.updateLastAccessed(jwt);
                }
            }
        } catch (ExpiredJwtException ex) {
            log.error("JWT token is expired: {}", ex.getMessage());
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