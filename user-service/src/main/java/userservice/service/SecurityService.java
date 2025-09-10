package userservice.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import userservice.model.Address;
import userservice.model.UserSession;
import userservice.repository.AddressRepository;
import userservice.repository.UserSessionRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final AddressRepository addressRepository;
    private final UserSessionRepository sessionRepository;

    /**
     * Get the current authenticated user ID
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Get the current authenticated user's role
     */
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        }
        return null;
    }

    /**
     * Check if current user is admin
     */
    public boolean isAdmin() {
        return "ADMIN".equals(getCurrentUserRole());
    }

    /**
     * Check if current user is business owner or admin
     */
    public boolean isBusinessOwnerOrAdmin() {
        String role = getCurrentUserRole();
        return "BUSINESS_OWNER".equals(role) || "ADMIN".equals(role);
    }

    /**
     * Check if current user can access the specified user's data
     * - CUSTOMER: Only their own data
     * - BUSINESS_OWNER: Their own data + can view customer data
     * - ADMIN: All data
     */
    public boolean canAccessUserData(Long targetUserId) {
        Long currentUserId = getCurrentUserId();
        String currentRole = getCurrentUserRole();

        if (currentUserId == null || currentRole == null) {
            return false;
        }

        // Admin can access all data
        if ("ADMIN".equals(currentRole)) {
            return true;
        }

        // Users can always access their own data
        if (currentUserId.equals(targetUserId)) {
            return true;
        }

        // Business owners can view (but not modify) customer data
        if ("BUSINESS_OWNER".equals(currentRole)) {
            // This would require checking if targetUserId is a customer
            // For now, we'll allow business owners to view any user data
            return true;
        }

        // Customers can only access their own data
        return false;
    }

    /**
     * Check if current user can modify the specified user's data
     * - CUSTOMER: Only their own data
     * - BUSINESS_OWNER: Only their own data
     * - ADMIN: All data
     */
    public boolean canModifyUserData(Long targetUserId) {
        Long currentUserId = getCurrentUserId();
        String currentRole = getCurrentUserRole();

        if (currentUserId == null || currentRole == null) {
            return false;
        }

        // Admin can modify all data
        if ("ADMIN".equals(currentRole)) {
            return true;
        }

        // All other roles can only modify their own data
        return currentUserId.equals(targetUserId);
    }

    /**
     * Check if current user can modify their own session
     * Used in UserSessionController for session management operations
     */
    public boolean canModifyOwnSession(String sessionToken) {
        Long currentUserId = getCurrentUserId();
        String currentRole = getCurrentUserRole();

        if (currentUserId == null || currentRole == null) {
            return false;
        }

        // Admin can modify any session
        if ("ADMIN".equals(currentRole)) {
            return true;
        }

        // Find the session and check if it belongs to current user
        return sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken)
                .map(session -> session.getUser().getId().equals(currentUserId))
                .orElse(false);

    }

    /**
     * Check if current user owns the specified address
     */
    public boolean ownsAddress(Long addressId) {
        Long currentUserId = getCurrentUserId();
        String currentRole = getCurrentUserRole();

        if (currentUserId == null) {
            return false;
        }

        // Admin can access all addresses
        if ("ADMIN".equals(currentRole)) {
            return true;
        }

        // Check if the address belongs to the current user
        Optional<Address> address = addressRepository.findById(addressId);
        return address.isPresent() && address.get().getUser().getId().equals(currentUserId);
    }

    /**
     * Check if current user can access addresses for the specified user
     */
    public boolean canAccessAddresses(Long targetUserId) {
        return canAccessUserData(targetUserId);
    }

    /**
     * Check if current user can modify addresses for the specified user
     */
    public boolean canModifyAddresses(Long targetUserId) {
        return canModifyUserData(targetUserId);
    }
}
