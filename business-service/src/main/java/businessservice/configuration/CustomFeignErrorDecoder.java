package businessservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import businessservice.exceptions.InvalidBusinessDataException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        int status = response.status();

        log.error("Feign client error - Method: {}, Status: {}, URL: {}",
                methodKey, status, requestUrl);

        String errorMessage = extractErrorMessage(response);

        switch (status) {
            case 400:
                log.error("Bad Request (400) from user-service: {}", errorMessage);
                return new InvalidBusinessDataException(
                        "Invalid request to user service: " + errorMessage
                );

            case 404:
                log.error("Not Found (404) from user-service: {}", errorMessage);
                return new InvalidBusinessDataException(
                        "User not found in the system"
                );

            case 500:
            case 503:
                log.error("Server Error ({}) from user-service: {}", status, errorMessage);
                return new InvalidBusinessDataException(
                        "User service is temporarily unavailable. Please try again in a few minutes."
                );

            case 401:
            case 403:
                log.error("Unauthorized/Forbidden ({}) from user-service: {}", status, errorMessage);
                return new InvalidBusinessDataException(
                        "Authentication failed with user service"
                );

            default:
                log.error("Unknown error ({}) from user-service: {}", status, errorMessage);
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }

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
                return body;
            }
        } catch (IOException e) {
            log.error("Error reading response body", e);
            return "Error reading response from user service";
        }
    }

    // Inner class to parse error responses
    @Setter
    private static class ErrorResponse {
        private String message;
        private String error;

        public String getMessage() {
            return message != null ? message : error;
        }

    }
}