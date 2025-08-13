package productservice.mapper;

import org.springframework.stereotype.Component;
import productservice.dto.*;
import productservice.model.Product;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public Product toEntity(ProductCreateRequest request) {
        if (request == null) {
            return null;
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .category(request.getCategory())
                .type(request.getType())
                .basePrice(request.getBasePrice())
                .discountedPrice(request.getDiscountedPrice())
                .availableQuantity(request.getAvailableQuantity())
                .minOrderQuantity(request.getMinOrderQuantity())
                .maxOrderQuantity(request.getMaxOrderQuantity())
                .unit(request.getUnit())
                .businessId(request.getBusinessId())
                .brand(request.getBrand())
                .imageUrl(request.getImageUrl())
                .additionalImages(request.getAdditionalImages())
                .isActive(true)
                .isAvailable(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Map specifications
        if (request.getSpecifications() != null) {
            List<Product.ProductSpecification> specifications = request.getSpecifications().stream()
                    .map(specRequest -> Product.ProductSpecification.builder()
                            .specKey(specRequest.getSpecKey())
                            .specValue(specRequest.getSpecValue())
                            .unit(specRequest.getUnit())
                            .createdAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
            product.setSpecifications(specifications);
        }

        // Map pricing tiers
        if (request.getPricingTiers() != null) {
            List<Product.ProductPricing> pricingTiers = request.getPricingTiers().stream()
                    .map(pricingRequest -> Product.ProductPricing.builder()
                            .minQuantity(pricingRequest.getMinQuantity())
                            .maxQuantity(pricingRequest.getMaxQuantity())
                            .pricePerUnit(pricingRequest.getPricePerUnit())
                            .discountPercentage(pricingRequest.getDiscountPercentage())
                            .isActive(true)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
            product.setPricingTiers(pricingTiers);
        }

        // Create embedded inventory
        Product.ProductInventory inventory = Product.ProductInventory.builder()
                .currentStock(request.getInitialStock())
                .reservedStock(0)
                .minStockLevel(request.getMinStockLevel())
                .maxStockLevel(request.getMaxStockLevel())
                .reorderPoint(request.getReorderPoint())
                .reorderQuantity(request.getReorderQuantity())
                .warehouseLocation(request.getWarehouseLocation())
                .lastUpdated(LocalDateTime.now())
                .build();
        product.setInventory(inventory);

        return product;
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .category(product.getCategory())
                .type(product.getType())
                .basePrice(product.getBasePrice())
                .discountedPrice(product.getDiscountedPrice())
                .availableQuantity(product.getAvailableQuantity())
                .minOrderQuantity(product.getMinOrderQuantity())
                .maxOrderQuantity(product.getMaxOrderQuantity())
                .unit(product.getUnit())
                .businessId(product.getBusinessId())
                .isActive(product.getIsActive())
                .isAvailable(product.getIsAvailable())
                .brand(product.getBrand())
                .imageUrl(product.getImageUrl())
                .additionalImages(product.getAdditionalImages())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();

        // Map specifications
        if (product.getSpecifications() != null) {
            List<ProductSpecificationResponse> specifications = product.getSpecifications().stream()
                    .map(this::toSpecificationResponse)
                    .collect(Collectors.toList());
            response.setSpecifications(specifications);
        }

        // Map pricing tiers
        if (product.getPricingTiers() != null) {
            List<ProductPricingResponse> pricingTiers = product.getPricingTiers().stream()
                    .map(this::toPricingResponse)
                    .collect(Collectors.toList());
            response.setPricingTiers(pricingTiers);
        }

        // Map inventory
        if (product.getInventory() != null) {
            response.setInventory(toInventoryResponse(product.getInventory()));
        }

        return response;
    }

    public void updateEntity(Product product, ProductUpdateRequest request) {
        if (request == null || product == null) {
            return;
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setCategory(request.getCategory());
        product.setType(request.getType());
        product.setBasePrice(request.getBasePrice());
        product.setDiscountedPrice(request.getDiscountedPrice());
        product.setAvailableQuantity(request.getAvailableQuantity());
        product.setMinOrderQuantity(request.getMinOrderQuantity());
        product.setMaxOrderQuantity(request.getMaxOrderQuantity());
        product.setUnit(request.getUnit());
        product.setBrand(request.getBrand());
        product.setImageUrl(request.getImageUrl());
        product.setAdditionalImages(request.getAdditionalImages());
        product.setUpdatedAt(LocalDateTime.now());

        // Update specifications
        if (request.getSpecifications() != null) {
            List<Product.ProductSpecification> specifications = request.getSpecifications().stream()
                    .map(specRequest -> Product.ProductSpecification.builder()
                            .specKey(specRequest.getSpecKey())
                            .specValue(specRequest.getSpecValue())
                            .unit(specRequest.getUnit())
                            .createdAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
            product.setSpecifications(specifications);
        } else {
            product.setSpecifications(new ArrayList<>());
        }

        // Update pricing tiers
        if (request.getPricingTiers() != null) {
            List<Product.ProductPricing> pricingTiers = request.getPricingTiers().stream()
                    .map(pricingRequest -> Product.ProductPricing.builder()
                            .minQuantity(pricingRequest.getMinQuantity())
                            .maxQuantity(pricingRequest.getMaxQuantity())
                            .pricePerUnit(pricingRequest.getPricePerUnit())
                            .discountPercentage(pricingRequest.getDiscountPercentage())
                            .isActive(true)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
            product.setPricingTiers(pricingTiers);
        } else {
            product.setPricingTiers(new ArrayList<>());
        }
    }

    public ProductSpecificationResponse toSpecificationResponse(Product.ProductSpecification specification) {
        if (specification == null) {
            return null;
        }

        return ProductSpecificationResponse.builder()
                .specKey(specification.getSpecKey())
                .specValue(specification.getSpecValue())
                .unit(specification.getUnit())
                .createdAt(specification.getCreatedAt())
                .build();
    }

    public ProductPricingResponse toPricingResponse(Product.ProductPricing pricing) {
        if (pricing == null) {
            return null;
        }

        return ProductPricingResponse.builder()
                .minQuantity(pricing.getMinQuantity())
                .maxQuantity(pricing.getMaxQuantity())
                .pricePerUnit(pricing.getPricePerUnit())
                .discountPercentage(pricing.getDiscountPercentage())
                .isActive(pricing.getIsActive())
                .createdAt(pricing.getCreatedAt())
                .updatedAt(pricing.getUpdatedAt())
                .build();
    }

    public ProductInventoryResponse toInventoryResponse(Product.ProductInventory inventory) {
        if (inventory == null) {
            return null;
        }

        return ProductInventoryResponse.builder()
                .currentStock(inventory.getCurrentStock())
                .reservedStock(inventory.getReservedStock())
                .minStockLevel(inventory.getMinStockLevel())
                .maxStockLevel(inventory.getMaxStockLevel())
                .reorderPoint(inventory.getReorderPoint())
                .reorderQuantity(inventory.getReorderQuantity())
                .warehouseLocation(inventory.getWarehouseLocation())
                .lastUpdated(inventory.getLastUpdated())
                .build();
    }

    public List<ProductResponse> toResponseList(List<Product> products) {
        if (products == null) {
            return new ArrayList<>();
        }

        return products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Helper method to update inventory
    public void updateInventory(Product product, int stockChange, int reservedChange) {
        if (product.getInventory() != null) {
            Product.ProductInventory inventory = product.getInventory();
            inventory.setCurrentStock(inventory.getCurrentStock() + stockChange);
            inventory.setReservedStock(inventory.getReservedStock() + reservedChange);
            inventory.setLastUpdated(LocalDateTime.now());
        }
    }
}