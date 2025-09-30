package businessservice.configuration;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        // connectTimeout: 5 seconds
        // readTimeout: 10 seconds
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
        // period: initial interval
        // maxPeriod: max interval between retries
        // maxAttempts: total attempts (including first call)
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
        // Use BASIC for production, FULL for development/debugging
        return Logger.Level.BASIC;
    }
}