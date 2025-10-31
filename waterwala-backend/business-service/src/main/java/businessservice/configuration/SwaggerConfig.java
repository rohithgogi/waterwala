package businessservice.configuration;

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
        info = @Info(
                title = "Business Service API",
                version = "v1.0",
                description = "Comprehensive Business Management Service API for WaterWala platform",
                contact = @Contact(
                        name = "WaterWala Development Team",
                        email = "dev@waterwala.com",
                        url = "https://waterwala.com"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        description = "Development Server",
                        url = "http://localhost:8082"
                )
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
    public OpenAPI businessServiceOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Business Service API")
                        .version("v1.0")
                        .description("""
                                # Business Service API Documentation
                                
                                This API provides comprehensive business management functionality for the WaterWala platform.

                                ## Core Features
                                - **Business Registration** - Register new water supply businesses with complete details
                                - **Business Verification** - Admin verification workflow for new businesses
                                - **Profile Management** - Update business information, operating hours, and services
                                - **Search & Discovery** - Find businesses by location, service type, and ratings
                                - **Rating & Reviews** - Manage business ratings and review statistics
                                - **Order Management** - Track order statistics and completion rates

                                ## Authentication
                                Most endpoints require JWT authentication with appropriate roles:
                                - **BUSINESS_OWNER** - Required for business registration and profile management
                                - **ADMIN** - Required for business verification and admin operations
                                - **Public endpoints** - Search and view business profiles (no authentication required)

                                ## Business Registration Requirements
                                - Valid GST number (format: 07AAJCB1234Q1Z5)
                                - Business registration number (alphanumeric, 10-20 characters)
                                - At least one service offering
                                - Operating hours for all days of the week
                                - Valid business address with pincode

                                ## Error Handling
                                All responses follow a consistent format with appropriate HTTP status codes:
                                - `200` - Success
                                - `201` - Created
                                - `400` - Bad Request (validation errors)
                                - `401` - Unauthorized
                                - `403` - Forbidden
                                - `404` - Not Found
                                - `409` - Conflict (duplicate business)
                                - `500` - Internal Server Error

                                ## Integration
                                This service integrates with:
                                - **User Service** - For user validation during business registration
                                - **Eureka Discovery Server** - For service registration and discovery
                                """));
    }
}