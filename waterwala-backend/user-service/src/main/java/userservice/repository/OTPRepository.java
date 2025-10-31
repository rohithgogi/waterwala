package userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import userservice.enums.OTPType;
import userservice.model.OTP;
import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {

    Optional<OTP> findTopByEmailAndTypeOrderByCreatedAtDesc(String email, OTPType type);

    // Find latest OTP by phone and type (for phone verification & login)
    Optional<OTP> findTopByPhoneAndTypeOrderByCreatedAtDesc(String phone, OTPType type);
}