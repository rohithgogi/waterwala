package businessservice.repository;

import businessservice.model.Business;
import businessservice.model.BusinessStatus;
import businessservice.model.ServiceType;
import businessservice.model.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    Optional<Business> findByUserId(Long userId);

    Optional<Business> findByBusinessRegistrationNumber(String businessRegistrationNumber);

    List<Business> findByStatusAndIsActive(BusinessStatus status, Boolean isActive);

    List<Business> findByVerificationStatus(VerificationStatus verificationStatus);

    @Query("SELECT b FROM Business b JOIN b.addresses ba WHERE ba.pincode = :pincode AND b.isActive = true AND b.isAvailable = true")
    List<Business> findByPincode(@Param("pincode") String pincode);

    @Query("SELECT b FROM Business b JOIN b.addresses ba WHERE ba.city = :city AND b.isActive = true AND b.isAvailable = true")
    List<Business> findByCity(@Param("city") String city);

    @Query("SELECT b FROM Business b JOIN b.services bs WHERE bs.serviceType = :serviceType AND b.isActive = true AND b.isAvailable = true")
    List<Business> findByServiceType(@Param("serviceType") ServiceType serviceType);

    @Query("SELECT b FROM Business b WHERE b.businessName LIKE %:keyword% OR b.description LIKE %:keyword% AND b.isActive = true")
    Page<Business> searchBusinesses(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Business b WHERE b.isActive = true AND b.isAvailable = true ORDER BY b.averageRating DESC, b.totalOrders DESC")
    Page<Business> findFeaturedBusinesses(Pageable pageable);

}
