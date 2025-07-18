package businessservice.service;

import businessservice.dto.BusinessRegistrationDto;
import businessservice.dto.BusinessUpdateDto;
import businessservice.exceptions.InvalidBusinessDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessValidationService {
    private static final Pattern GST_PATTERN = Pattern.compile("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}[Z]{1}[1-9A-Z]{1}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]{1}\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PINCODE_PATTERN=Pattern.compile("^[1-9][0-9]{5}$");

    public void validateBusinessRegistration(BusinessRegistrationDto registrationDto){
        log.info("Validating business registration data for {}",registrationDto.getBusinessName());

        if(!GST_PATTERN.matcher(registrationDto.getGstNumber()).matches()){
            throw new InvalidBusinessDataException("Invalid GST Number format");
        }
        if (!PHONE_PATTERN.matcher(registrationDto.getContactPhone()).matches()) {
            throw new InvalidBusinessDataException("Invalid phone number format");
        }
        if (!EMAIL_PATTERN.matcher(registrationDto.getContactEmail()).matches()) {
            throw new InvalidBusinessDataException("Invalid email format");
        }
        if (registrationDto.getAddress() != null) {
            if (!PINCODE_PATTERN.matcher(registrationDto.getAddress().getPincode()).matches()) {
                throw new InvalidBusinessDataException("Invalid pincode format");
            }
        }

        // Validate services
        if (registrationDto.getServices() == null || registrationDto.getServices().isEmpty()) {
            throw new InvalidBusinessDataException("At least one service must be provided");
        }

        // Validate operating hours
        if (registrationDto.getOperatingHours() == null || registrationDto.getOperatingHours().isEmpty()) {
            throw new InvalidBusinessDataException("Operating hours must be provided");
        }

        // Validate operating hours logic
        registrationDto.getOperatingHours().forEach(hours -> {
            if (hours.getIsOpen() && !hours.getIs24Hours()) {
                if (hours.getOpenTime() == null || hours.getCloseTime() == null) {
                    throw new InvalidBusinessDataException("Open and close times are required when business is open");
                }
                if (hours.getOpenTime().isAfter(hours.getCloseTime())) {
                    throw new InvalidBusinessDataException("Open time cannot be after close time");
                }
            }
        });

        log.info("Business registration validation completed successfully");
    }

    public void validateBusinessUpdate(BusinessUpdateDto updateDto) {
        log.info("Validating business update data");

        if (updateDto.getContactPhone() != null && !PHONE_PATTERN.matcher(updateDto.getContactPhone()).matches()) {
            throw new InvalidBusinessDataException("Invalid phone number format");
        }

        if (updateDto.getContactEmail() != null && !EMAIL_PATTERN.matcher(updateDto.getContactEmail()).matches()) {
            throw new InvalidBusinessDataException("Invalid email format");
        }

        log.info("Business update validation completed successfully");
    }
}
