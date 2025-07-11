package productservice.mapper;

import org.springframework.stereotype.Component;
import productservice.dto.*;
import productservice.model.Product;
import productservice.model.ProductPricing;
import productservice.model.ProductSpecification;
import productservice.model.ProductInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper  {
    public Product toEntity(ProductCreateRequest request){
        if(request == null){
            return null;
        }
        Product product=Product.builder()
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
                .build();
        //Map specifications
        if (request.getSpecifications() != null) {
            List<ProductSpecification> specifications = request.getSpecifications().stream()
                    .map(specRequest -> ProductSpecification.builder()
                            .product(product)
                            .specKey(specRequest.getSpecKey())
                            .specValue(specRequest.getSpecValue())
                            .unit(specRequest.getUnit())
                            .build())
                    .collect(Collectors.toList());
            product.setSpecifications(specifications);
        }

        // Map pricing tiers
        if (request.getPricingTiers() != null) {
            List<ProductPricing> pricingTiers = request.getPricingTiers().stream()
                    .map(pricingRequest -> ProductPricing.builder()
                            .product(product)
                            .minQuantity(pricingRequest.getMinQuantity())
                            .maxQuantity(pricingRequest.getMaxQuantity())
                            .pricePerUnit(pricingRequest.getPricePerUnit())
                            .discountPercentage(pricingRequest.getDiscountPercentage())
                            .isActive(true)
                            .build())
                    .collect(Collectors.toList());
            product.setPricingTiers(pricingTiers);
        }

        return product;
    }

    public ProductResponse toResponse(Product product){
        if(product == null){
            return null;
        }
         ProductResponse response=ProductResponse.builder()
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
        return response;
    }
    public ProductResponse toResponseWithInventory(Product product, ProductInventory inventory) {
        ProductResponse response = toResponse(product);
        if (inventory != null) {
            response.setInventory(toInventoryResponse(inventory));
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

        // Update specifications
        if (request.getSpecifications() != null) {
            // Clear existing specifications
            if (product.getSpecifications() != null) {
                product.getSpecifications().clear();
            } else {
                product.setSpecifications(new ArrayList<>());
            }

            // Add new specifications
            List<ProductSpecification> specifications = request.getSpecifications().stream()
                    .map(specRequest -> ProductSpecification.builder()
                            .product(product)
                            .specKey(specRequest.getSpecKey())
                            .specValue(specRequest.getSpecValue())
                            .unit(specRequest.getUnit())
                            .build())
                    .collect(Collectors.toList());
            product.getSpecifications().addAll(specifications);
        }

        // Update pricing tiers
        if (request.getPricingTiers() != null) {
            // Clear existing pricing tiers
            if (product.getPricingTiers() != null) {
                product.getPricingTiers().clear();
            } else {
                product.setPricingTiers(new ArrayList<>());
            }

            // Add new pricing tiers
            List<ProductPricing> pricingTiers = request.getPricingTiers().stream()
                    .map(pricingRequest -> ProductPricing.builder()
                            .product(product)
                            .minQuantity(pricingRequest.getMinQuantity())
                            .maxQuantity(pricingRequest.getMaxQuantity())
                            .pricePerUnit(pricingRequest.getPricePerUnit())
                            .discountPercentage(pricingRequest.getDiscountPercentage())
                            .isActive(true)
                            .build())
                    .collect(Collectors.toList());
            product.getPricingTiers().addAll(pricingTiers);
        }
    }

    public ProductSpecificationResponse toSpecificationResponse(ProductSpecification specification) {
        if (specification == null) {
            return null;
        }

        return ProductSpecificationResponse.builder()
                .id(specification.getId())
                .specKey(specification.getSpecKey())
                .specValue(specification.getSpecValue())
                .unit(specification.getUnit())
                .createdAt(specification.getCreatedAt())
                .build();
    }

    public ProductPricingResponse toPricingResponse(ProductPricing pricing) {
        if (pricing == null) {
            return null;
        }

        return ProductPricingResponse.builder()
                .id(pricing.getId())
                .minQuantity(pricing.getMinQuantity())
                .maxQuantity(pricing.getMaxQuantity())
                .pricePerUnit(pricing.getPricePerUnit())
                .discountPercentage(pricing.getDiscountPercentage())
                .isActive(pricing.getIsActive())
                .createdAt(pricing.getCreatedAt())
                .updatedAt(pricing.getUpdatedAt())
                .build();
    }

    public ProductInventoryResponse toInventoryResponse(ProductInventory inventory) {
        if (inventory == null) {
            return null;
        }

        return ProductInventoryResponse.builder()
                .id(inventory.getId())
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

    public ProductSpecification toSpecificationEntity(ProductSpecificationRequest request, Product product) {
        if (request == null) {
            return null;
        }

        return ProductSpecification.builder()
                .product(product)
                .specKey(request.getSpecKey())
                .specValue(request.getSpecValue())
                .unit(request.getUnit())
                .build();
    }

    public ProductPricing toPricingEntity(ProductPricingRequest request, Product product) {
        if (request == null) {
            return null;
        }

        return ProductPricing.builder()
                .product(product)
                .minQuantity(request.getMinQuantity())
                .maxQuantity(request.getMaxQuantity())
                .pricePerUnit(request.getPricePerUnit())
                .discountPercentage(request.getDiscountPercentage())
                .isActive(true)
                .build();
    }

}
