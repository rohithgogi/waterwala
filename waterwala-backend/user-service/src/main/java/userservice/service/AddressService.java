package userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import userservice.dto.AddressDto;
import userservice.dto.AddressResponseDto;
import userservice.exceptions.AddressNotFoundException;
import userservice.exceptions.UserNotFoundException;
import userservice.model.Address;
import userservice.model.User;
import userservice.repository.AddressRepository;
import userservice.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('BUSINESS_OWNER', 'CUSTOMER') and #userId==authentication.principal)")
    public AddressResponseDto addAddress(Long userId, AddressDto addressDto){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User not found with ID: "+userId));

        if(addressDto.getIsDefault() || addressRepository.existsByUserId(userId)){
            addressRepository.updateDefaultStatusByUserId(userId,false);
        }

        Address address = Address.builder()
                .user(user)
                .addressLine1(addressDto.getAddressLine1())
                .addressLine2(addressDto.getAddressLine2())
                .landmark(addressDto.getLandmark())
                .city(addressDto.getCity())
                .state(addressDto.getState())
                .pincode(addressDto.getPincode())
                .country(addressDto.getCountry() != null ? addressDto.getCountry() : "India")
                .type(addressDto.getType())
                .isDefault(addressDto.getIsDefault() || !addressRepository.existsByUserId(userId))
                .latitude(addressDto.getLatitude())
                .longitude(addressDto.getLongitude())
                .build();
        Address savedAddress=addressRepository.save(address);
        return convertToResponseDto(savedAddress);
    }
    @PreAuthorize("hasRole('ADMIN') or @addressService.isAddressOwner(#addressId, authentication.principal)")
    public AddressResponseDto updateAddress(Long addressId, AddressDto addressDto){
        Address address= addressRepository.findById(addressId)
                .orElseThrow(()-> new AddressNotFoundException("Address not found with ID: "+addressId));

        address.setAddressLine1(addressDto.getAddressLine1());
        address.setAddressLine2(addressDto.getAddressLine2());
        address.setLandmark(addressDto.getLandmark());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setPincode(addressDto.getPincode());
        address.setCountry(addressDto.getCountry() != null ? addressDto.getCountry() : "India");
        address.setType(addressDto.getType());
        address.setLatitude(addressDto.getLatitude());
        address.setLongitude(addressDto.getLongitude());

        if(addressDto.getIsDefault() && !address.getIsDefault()){
            addressRepository.updateDefaultStatusByUserId(address.getUser().getId(),false);
            address.setIsDefault(true);
        }

        Address updatedAddress = addressRepository.save(address);
        return convertToResponseDto(updatedAddress);
    }


    @PreAuthorize("hasRole('ADMIN') or @addressService.isAddressOwner(#addressId, authentication.principal)")
    public void deleteAddress(Long addressId){
        Address address=addressRepository.findById(addressId)
                .orElseThrow(()->new AddressNotFoundException("Address not found with ID: "+addressId));

        addressRepository.delete(address);
    }

    public void setDefaultAddress(Long addressId){
        Address address=addressRepository.findById(addressId)
                .orElseThrow(()->new AddressNotFoundException("Address not found with ID: "+addressId));
        //Remove other addresss as default
        addressRepository.updateDefaultStatusByUserId(address.getUser().getId(),false);

        //set the address as default
        address.setIsDefault(true);
        addressRepository.save(address);
    }
    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('BUSINESS_OWNER', 'CUSTOMER') and #userId == authentication.principal)")
    public List<AddressResponseDto> getAllAddresses(Long userID){
        List<Address> addresses=addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userID);
        return addresses.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

    }

    public AddressResponseDto getDefaultAddress(Long userId){
        Address address=addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(()-> new AddressNotFoundException("Default address not found with ID: "+userId));
        return convertToResponseDto(address);
    }

    public AddressResponseDto getAddressById(Long addressId){
        Address address=addressRepository.findById(addressId)
                .orElseThrow(()->new AddressNotFoundException("Address not found with ID: "+addressId));
        return convertToResponseDto(address);
    }
    private AddressResponseDto convertToResponseDto(Address address) {
        return AddressResponseDto.builder()
                .id(address.getId())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .country(address.getCountry())
                .type(address.getType())
                .isDefault(address.getIsDefault())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
