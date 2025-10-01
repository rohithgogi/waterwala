package businessservice.configuration;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final CustomFeignErrorDecoder customFeignErrorDecoder;

    /**
     * Configure Feign request timeouts
     * - Connect timeout: Time to establish connection
     * - Read timeout: Time to read response after connection established
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                5, TimeUnit.SECONDS,  // Connect timeout
                10, TimeUnit.SECONDS,  // Read timeout
                true                   // Follow redirects
        );
    }

    /**
     * Custom error decoder for better error handling
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return customFeignErrorDecoder;
    }

    /**
     * Configure retry mechanism
     * - Retry on connection failures
     * - Max 3 attempts
     * - 1 second interval between retries
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(
                1000,  // Start with 1 second
                2000,  // Max 2 seconds between retries
                3      // Max 3 attempts total
        );
    }

    /**
     * Enable detailed Feign logging for debugging
     * Levels: NONE, BASIC, HEADERS, FULL
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * JWT Token Propagation Interceptor
     * Forwards the Authorization header from incoming request to Feign client requests
     */
    @Bean
    public RequestInterceptor jwtTokenInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }
}