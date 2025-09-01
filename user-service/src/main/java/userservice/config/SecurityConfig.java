package userservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import userservice.security.JwtAuthenticationEntryPoint;
import userservice.security.JwtAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers(
                                "/api/v1/auth/send-otp",
                                "/api/v1/auth/login",
                                "/api/v1/users/register",  // Specific registration endpoint
                                "/api/v1/users/exists/**", // Specific exists endpoints
                                "/api/v1/otp/send/**",
                                "/api/v1/otp/verify",
                                "/api/v1/otp/status",
                                "/api/v1/sessions/create",
                                "/api/v1/sessions/refresh",
                                "/api/v1/sessions/validate",
                                // Swagger endpoints
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/swagger-resources/**"
                        ).permitAll()

                        // Admin only endpoints
                        .requestMatchers(
                                "/api/v1/users", // GET all users
                                "/api/v1/users/status/**",
                                "/api/v1/users/role/**",
                                "/api/v1/users/*/status" // Update user status
                        ).hasRole("ADMIN")

                        // Business owner and admin endpoints
                        .requestMatchers(
                                "/api/v1/users/phone/**",
                                "/api/v1/users/email/**"
                        ).hasAnyRole("BUSINESS_OWNER", "ADMIN")

                        // Authenticated user endpoints (requires valid JWT and role)
                        .requestMatchers(
                                "/api/v1/users/id/**",
                                "/api/v1/users/*/verify-phone",
                                "/api/v1/users/*/verify-email",     // This now requires authentication
                                "/api/v1/users/*/last-login"
                        ).hasAnyRole("CUSTOMER", "BUSINESS_OWNER", "ADMIN")

                        // General user endpoints (authenticated users)
                        .requestMatchers("/api/v1/users/*")
                        .hasAnyRole("CUSTOMER", "BUSINESS_OWNER", "ADMIN")

                        // Address and session management
                        .requestMatchers("/api/v1/addresses/**")
                        .hasAnyRole("CUSTOMER", "BUSINESS_OWNER", "ADMIN")

                        .requestMatchers("/api/v1/sessions/**")
                        .hasAnyRole("CUSTOMER", "BUSINESS_OWNER", "ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}