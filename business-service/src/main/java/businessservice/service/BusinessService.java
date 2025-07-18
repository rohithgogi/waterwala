package businessservice.service;

import businessservice.dto.BusinessProfileDto;
import businessservice.dto.BusinessRegistrationDto;
import businessservice.dto.BusinessSearchDto;
import businessservice.dto.BusinessUpdateDto;
import businessservice.exceptions.BusinessAlreadyExistsException;
import businessservice.exceptions.BusinessNotFoundException;
import businessservice.exceptions.UnauthorizedAccessException;
import businessservice.model.Business;
import businessservice.model.BusinessStatus;
import businessservice.model.VerificationStatus;
import businessservice.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final BusinessValidationService validationService;

    public BusinessProfileDto registerBusiness(BusinessRegistrationDto registrationDto){
        log.info("Registering new business: {}",registrationDto.getBusinessName());

        validationService.validateBusinessRegistration(registrationDto);

        if(businessRepository.findByBusinessRegistrationNumber(registrationDto.getBusinessRegistrationNumber()).isPresent()){
            throw new BusinessAlreadyExistsException("Business with this registration number already exists");
        }

        if (businessRepository.findByUserId(registrationDto.getUserId()).isPresent()) {
            throw new BusinessAlreadyExistsException("Business already registered for this user");
        }

        Business business = Business.builder()
                .userId(registrationDto.getUserId())
                .businessName(registrationDto.getBusinessName())
                .businessType(registrationDto.getBusinessType())
                .businessRegistrationNumber(registrationDto.getBusinessRegistrationNumber())
                .gstNumber(registrationDto.getGstNumber())
                .contactPersonName(registrationDto.getContactPersonName())
                .contactEmail(registrationDto.getContactEmail())
                .contactPhone(registrationDto.getContactPhone())
                .description(registrationDto.getDescription())
                .status(BusinessStatus.PENDING_VERIFICATION)
                .verificationStatus(VerificationStatus.PENDING)
                .isActive(true)
                .isAvailable(false)
                .averageRating(0.0)
                .totalReviews(0)
                .totalOrders(0)
                .completedOrders(0)
                .logoUrl(registrationDto.getLogoUrl())
                .build();

        Business savedBusiness = businessRepository.save(business);

        log.info("Business registered successfully with ID: {}", savedBusiness.getId());
        return convertToProfileDto(savedBusiness);
    }
    @Transactional(readOnly = true)
    public BusinessProfileDto getBusinessProfile(Long businessId){
        Business business=businessRepository.findById(businessId)
                .orElseThrow(()-> new BusinessNotFoundException("Business not found with ID: "+businessId));
        return convertToProfileDto(business);
    }
    @Transactional(readOnly = true)
    public BusinessProfileDto getBusinessByUserId(Long userId){
        Business business=businessRepository.findByUserId(userId)
                .orElseThrow(()-> new BusinessNotFoundException("Business not found with user ID: "+userId));
        return convertToProfileDto(business);
    }
    @Transactional
    public BusinessProfileDto updateBusinessProfile(Long businessId, BusinessUpdateDto updateDto, Long userId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business not found with ID: " + businessId));

        // Check if user is authorized to update this business
        if (!business.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("User not authorized to update this business");
        }

        // Update business fields
        if (updateDto.getBusinessName() != null) {
            business.setBusinessName(updateDto.getBusinessName());
        }
        if (updateDto.getDescription() != null) {
            business.setDescription(updateDto.getDescription());
        }
        if (updateDto.getContactPersonName() != null) {
            business.setContactPersonName(updateDto.getContactPersonName());
        }
        if (updateDto.getContactEmail() != null) {
            business.setContactEmail(updateDto.getContactEmail());
        }
        if (updateDto.getContactPhone() != null) {
            business.setContactPhone(updateDto.getContactPhone());
        }
        if (updateDto.getLogoUrl() != null) {
            business.setLogoUrl(updateDto.getLogoUrl());
        }
        if (updateDto.getIsAvailable() != null) {
            business.setIsAvailable(updateDto.getIsAvailable());
        }

        Business updatedBusiness = businessRepository.save(business);

        log.info("Business profile updated for ID: {}", businessId);
        return convertToProfileDto(updatedBusiness);
    }

    @Transactional
    public void verifyBusiness(Long businessId, VerificationStatus status, String comments) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business not found with ID: " + businessId));

        business.setVerificationStatus(status);

        if (status == VerificationStatus.APPROVED) {
            business.setStatus(BusinessStatus.VERIFIED);
            business.setVerifiedAt(LocalDateTime.now());
            business.setIsAvailable(true);
        }

        businessRepository.save(business);
        log.info("Business verification updated for ID: {}, Status: {}", businessId, status);
    }

    @Transactional(readOnly = true)
    public List<BusinessProfileDto> searchBusinesses(BusinessSearchDto searchDto) {
        List<Business> businesses;

        if (searchDto.getPincode() != null) {
            businesses = businessRepository.findByPincode(searchDto.getPincode());
        } else if (searchDto.getCity() != null) {
            businesses = businessRepository.findByCity(searchDto.getCity());
        } else if (searchDto.getServiceType() != null) {
            businesses = businessRepository.findByServiceType(searchDto.getServiceType());
        } else {
            businesses = businessRepository.findByStatusAndIsActive(BusinessStatus.VERIFIED, true);
        }

        return businesses.stream()
                .map(this::convertToProfileDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<BusinessProfileDto> getFeaturedBusinesses(Pageable pageable) {
        Page<Business> businesses = businessRepository.findFeaturedBusinesses(pageable);
        return businesses.map(this::convertToProfileDto);
    }

    @Transactional
    public void updateBusinessRating(Long businessId, Double rating, Integer reviewCount) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business not found with ID: " + businessId));

        business.setAverageRating(rating);
        business.setTotalReviews(reviewCount);
        businessRepository.save(business);

        log.info("Business rating updated for ID: {}, Rating: {}", businessId, rating);
    }

    @Transactional
    public void updateOrderStats(Long businessId, Integer totalOrders, Integer completedOrders) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business not found with ID: " + businessId));

        business.setTotalOrders(totalOrders);
        business.setCompletedOrders(completedOrders);

        businessRepository.save(business);
        log.info("Order stats updated for business ID: {}", businessId);
    }

    @Transactional
    public void deactivateBusiness(Long businessId, Long userId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business not found with ID: " + businessId));

        if (!business.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("User not authorized to deactivate this business");
        }

        business.setIsActive(false);
        business.setIsAvailable(false);
        business.setStatus(BusinessStatus.DEACTIVATED);

        businessRepository.save(business);
        log.info("Business deactivated for ID: {}", businessId);
    }

    public boolean isBusinessOwner(Long businessId, Long userId) {
        return businessRepository.findById(businessId)
                .map(business -> business.getUserId().equals(userId))
                .orElse(false);
    }

    private BusinessProfileDto convertToProfileDto(Business business) {
        return BusinessProfileDto.builder()
                .id(business.getId())
                .userId(business.getUserId())
                .businessName(business.getBusinessName())
                .businessType(business.getBusinessType())
                .contactPersonName(business.getContactPersonName())
                .contactEmail(business.getContactEmail())
                .contactPhone(business.getContactPhone())
                .description(business.getDescription())
                .status(business.getStatus())
                .verificationStatus(business.getVerificationStatus())
                .isActive(business.getIsActive())
                .isAvailable(business.getIsAvailable())
                .averageRating(business.getAverageRating())
                .totalReviews(business.getTotalReviews())
                .totalOrders(business.getTotalOrders())
                .completedOrders(business.getCompletedOrders())
                .logoUrl(business.getLogoUrl())
                .createdAt(business.getCreatedAt())
                .verifiedAt(business.getVerifiedAt())
                .build();
    }
}
