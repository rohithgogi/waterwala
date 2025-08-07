package userservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "User Service API",
        version = "v1.0",
        description = "Comprehensive User Management Service API with authentication, address management, and session handling",
        contact = @Contact(
                name = "User Service Team",
                email = "support@userservice.com",
                url = "https://userservice.com"
        ),
        license = @License(
                name = "MIT License",
                url = "https://opensource.org/licenses/MIT"
        )
        ),
        servers = {
                @Server(description = "Development Server"
                ,url = "http://localhost:8081")
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "JWT Authentication token. Format: Bearer {token}"
)
public class SwaggerConfig {
    @Bean
    public OpenAPI userServiceOpenAPI(){
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("User Service API")
                        .version("v1.0")
                        .description("""
                                # User Service API Documentation
                                
                                This API provides comprehensive user management functionality including:

                                ## Core Features
                                - **User Registration & Authentication** - Complete user lifecycle management
                                - **OTP Verification** - Multi-channel OTP support (SMS, Email)
                                - **Address Management** - CRUD operations for user addresses
                                - **Session Management** - JWT-based session handling with refresh tokens
                                - **Role-based Access Control** - Support for different user roles

                                ## Authentication
                                Most endpoints require JWT authentication. Use the `/api/v1/auth/login` endpoint to obtain a token.

                                ## Error Handling
                                All responses follow a consistent format with appropriate HTTP status codes.

                                ## Rate Limiting
                                Some endpoints may have rate limiting applied, especially OTP-related endpoints.
                                """));
    }
}
