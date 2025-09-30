package businessservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for Business Service
 *
 * Features:
 * - Business registration and management
 * - Integration with User Service via Feign
 * - Service discovery with Eureka
 * - JWT-based authentication
 */
@SpringBootApplication
@EnableFeignClients
public class BusinessServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusinessServiceApplication.class, args);
    }
}