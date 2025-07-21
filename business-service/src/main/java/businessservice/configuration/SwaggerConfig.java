package businessservice.configuration;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI businessServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WaterWala Business Service API")
                        .description("API for managing business operations in WaterWala platform")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("WaterWala Development Team")
                                .email("dev@waterwala.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}