package productservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import productservice.exceptions.InvalidProductDataException;
import productservice.exceptions.ProductServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Custom error decoder for Feign clients
 * Translates HTTP errors from external services into meaningful application exceptions
 */
@Slf4j
@Component
public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        int status = response.status();
        String serviceName = extractServiceName(methodKey);

        log.error("Feign client error - Service: {}, Method: {}, Status: {}, URL: {}",
                serviceName, methodKey, status, requestUrl);

        String errorMessage = extractErrorMessage(response);

        switch (status) {
            case 400:
                log.error("Bad Request (400) from {}: {}", serviceName, errorMessage);
                return new InvalidProductDataException(
                        String.format("Invalid request to %s: %s", serviceName, errorMessage)
                );

            case 404:
                log.error("Not Found (404) from {}: {}", serviceName, errorMessage);
                if (serviceName.contains("BusinessService")) {
                    return new InvalidProductDataException(
                            "Business not found. Please verify the business ID and try again."
                    );
                } else if (serviceName.contains("UserService")) {
                    return new InvalidProductDataException(
                            "User not found in the system"
                    );
                }
                return new InvalidProductDataException(
                        String.format("Resource not found in %s", serviceName)
                );

            case 500:
            case 503:
                log.error("Server Error ({}) from {}: {}", status, serviceName, errorMessage);
                return new ProductServiceException(
                        String.format("%s is temporarily unavailable. Please try again later.", serviceName)
                );

            case 401:
            case 403:
                log.error("Unauthorized/Forbidden ({}) from {}: {}", status, serviceName, errorMessage);
                return new ProductServiceException(
                        String.format("Authentication/Authorization failed with %s", serviceName)
                );

            case 409:
                log.error("Conflict (409) from {}: {}", serviceName, errorMessage);
                return new InvalidProductDataException(
                        String.format("Conflict in %s: %s", serviceName, errorMessage)
                );

            default:
                log.error("Unknown error ({}) from {}: {}", status, serviceName, errorMessage);
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }

    /**
     * Extracts the service name from the method key
     * Example: "BusinessServiceClient#validateBusiness(String)" -> "BusinessService"
     */
    private String extractServiceName(String methodKey) {
        if (methodKey != null && methodKey.contains("#")) {
            String className = methodKey.substring(0, methodKey.indexOf("#"));
            return className.replace("Client", "");
        }
        return "External Service";
    }

    /**
     * Extracts error message from response body
     */
    private String extractErrorMessage(Response response) {
        if (response.body() == null) {
            return "No error details available";
        }

        try (InputStream bodyStream = response.body().asInputStream()) {
            String body = new String(bodyStream.readAllBytes(), StandardCharsets.UTF_8);

            // Try to parse as JSON and extract message
            try {
                ErrorResponse errorResponse = objectMapper.readValue(body, ErrorResponse.class);
                return errorResponse.getMessage() != null ?
                        errorResponse.getMessage() : body;
            } catch (Exception e) {
                // If not valid JSON, return raw body
                return body.length() > 200 ? body.substring(0, 200) + "..." : body;
            }
        } catch (IOException e) {
            log.error("Error reading response body", e);
            return "Error reading response from external service";
        }
    }

    /**
     * Inner class to parse error responses
     */
    @Setter
    private static class ErrorResponse {
        private String message;
        private String error;

        public String getMessage() {
            return message != null ? message : error;
        }
    }
}