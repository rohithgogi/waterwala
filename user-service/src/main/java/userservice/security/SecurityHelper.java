package userservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import userservice.repository.AddressRepository;

@Component
@RequiredArgsConstructor
public class SecurityHelper {
    private final AddressRepository addressRepository;

    public boolean isAddressOwner(Long addressId,Long userId){
        return  addressRepository.existsByIdAndUserId(addressId,userId);
    }

    public boolean isOwnerOrAdmin(Long resourceUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = (Long) auth.getPrincipal();

        return hasRole("ADMIN") || resourceUserId.equals(currentUserId);
    }
    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
    public boolean isBusinessOwnerOrAdmin() {
        return hasRole("ADMIN") || hasRole("BUSINESS_OWNER");
    }

    public boolean isCustomerOrAdmin() {
        return hasRole("ADMIN") || hasRole("CUSTOMER");
    }
}
