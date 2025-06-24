package userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userservice.dto.AddressDto;
import userservice.dto.AddressResponseDto;
import userservice.dto.CommonResponseDto.ApiResponse;
import userservice.model.Address;
import userservice.repository.AddressRepository;
import userservice.service.AddressService;

import java.util.List;

@RestController
@RequestMapping("api/user-service/v1/addresses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<AddressResponseDto>> addAddress(@PathVariable Long userId, @Valid @RequestBody AddressDto addressDto){
        AddressResponseDto address= addressService.addAddress(userId,addressDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address added successfully",address));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponseDto>> updateAddress(@PathVariable Long addressId,@Valid @RequestBody AddressDto addressDto){
        AddressResponseDto address=addressService.updateAddress(addressId,addressDto);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully",address));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<String>> deleteAddress(@PathVariable Long addressId){
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully"));
    }

    @PatchMapping("/{addressId}/set-default")
    public ResponseEntity<ApiResponse<AddressResponseDto>> setDefaultAddress(@PathVariable Long addressId){
        addressService.setDefaultAddress(addressId);
        return ResponseEntity.ok(ApiResponse.success("Default address updated successfully"));
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<ApiResponse<List<AddressResponseDto>>> getAllAddressesOfUser(@PathVariable Long userId){
        List<AddressResponseDto> addresses=addressService.getAllAddresses(userId);
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved successfully",addresses));
    }

    @GetMapping("user/{userId}/default")
    public ResponseEntity<ApiResponse<AddressResponseDto>> getDefaultAddress(@PathVariable Long userId){
        AddressResponseDto address=addressService.getDefaultAddress(userId);
        return ResponseEntity.ok(ApiResponse.success("Default address retrieved successfully",address));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponseDto>> getAddressById(@PathVariable Long addressId){
        AddressResponseDto address= addressService.getAddressById(addressId);
        return ResponseEntity.ok(ApiResponse.success("Address retrieved successfully",address));
    }

}
