package productservice.configuration;

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
                title = "Product Service API",
                version = "v1.0",
                description = "Comprehensive Product Catalog Service API with inventory, pricing, and specifications",
                contact = @Contact(
                        name = "Product Service Team",
                        email = "support@productservice.com",
                        url = "https://productservice.com"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(description = "Development Server", url = "http://localhost:8082")
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
    public OpenAPI productServiceOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Product Service API")
                        .version("v1.0")
                        .description("""
                                # Product Service API Documentation
                                
                                This API manages the product catalog including inventory, specifications, and pricing.

                                ## Core Features
                                - **Product Management** - CRUD operations for products
                                - **Inventory Tracking** - Stock level monitoring
                                - **Pricing** - Base and discounted prices, bulk pricing tiers
                                - **Specifications** - Flexible product specifications

                                ## Authentication
                                All secured endpoints require JWT authentication from the User Service login endpoint.

                                ## Error Handling
                                Consistent error format with appropriate HTTP status codes.
                                """));
    }
}
