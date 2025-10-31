package productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import productservice.dto.*;
import productservice.model.ProductCategory;
import productservice.model.ProductType;
import productservice.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "Comprehensive APIs for managing water delivery products including inventory and pricing")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/create")
    @Operation(
            summary = "Create a new product",
            description = "Create a new product with inventory information. Automatically validates business and sets up initial inventory tracking."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Product with SKU already exists")
    })
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        log.info("Creating product with SKU: {}", request.getSku());
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/business/{businessId}")
    @Operation(summary = "Get all products by business",
            description = "Retrieves all products (active and inactive) for a specific business")
    public ResponseEntity<List<ProductResponse>> getProductsByBusiness(
            @Parameter(description = "Business ID") @PathVariable Long businessId) {
        log.debug("Getting products by business ID: {}", businessId);
        List<ProductResponse> response = productService.getProductByBusiness(String.valueOf(businessId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/active")
    @Operation(summary = "Get active products by business",
            description = "Retrieves only active products for a specific business")
    public ResponseEntity<List<ProductResponse>> getActiveProductsByBusiness(
            @Parameter(description = "Business ID") @PathVariable Long businessId) {
        log.debug("Getting active products by business ID: {}", businessId);
        List<ProductResponse> response = productService.getActiveProductByBusiness(String.valueOf(businessId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/available")
    @Operation(summary = "Get available products by business",
            description = "Retrieves available products for a specific business")
    public ResponseEntity<List<ProductResponse>> getAvailableProductsByBusiness(
            @Parameter(description = "Business ID") @PathVariable Long businessId) {
        log.debug("Getting available products by business ID: {}", businessId);
        List<ProductResponse> response = productService.getAvailableProductByBusiness(String.valueOf(businessId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}/business/{businessId}")
    @Operation(summary = "Get products by category and business",
            description = "Retrieves products by category for a specific business")
    public ResponseEntity<List<ProductResponse>> getProductsByCategoryAndBusiness(
            @Parameter(description = "Product category") @PathVariable ProductCategory category,
            @Parameter(description = "Business ID") @PathVariable Long businessId) {
        log.debug("Getting products by category: {} and business ID: {}", category, businessId);
        List<ProductResponse> response = productService.getProductByCategoryAndBusiness(category, String.valueOf(businessId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/page")
    @Operation(summary = "Get products by business with pagination",
            description = "Retrieves products by business with pagination support")
    public ResponseEntity<Page<ProductResponse>> getProductsByBusinessPaginated(
            @Parameter(description = "Business ID") @PathVariable Long businessId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDirection) {
        log.debug("Getting products by business ID: {} - page: {}, size: {}", businessId, page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> response = productService.getProductsByBusiness(String.valueOf(businessId), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/count")
    @Operation(summary = "Count products by business", description = "Returns the count of active products for a business")
    public ResponseEntity<Long> countProductsByBusiness(
            @Parameter(description = "Business ID") @PathVariable Long businessId) {
        log.debug("Counting products by business ID: {}", businessId);
        long count = productService.countProductsByBusiness(String.valueOf(businessId));
        return ResponseEntity.ok(count);
    }

    @GetMapping("/business/{businessId}/top-selling")
    @Operation(summary = "Get top selling products", description = "Retrieves top selling products for a business")
    public ResponseEntity<List<ProductResponse>> getTopSellingProducts(
            @Parameter(description = "Business ID") @PathVariable Long businessId,
            @Parameter(description = "Maximum number of products to return") @RequestParam(defaultValue = "10") int limit) {
        log.debug("Getting top {} selling products for business ID: {}", limit, businessId);
        List<ProductResponse> response = productService.getTopSellingProducts(String.valueOf(businessId), limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/recent")
    @Operation(summary = "Get recently added products", description = "Retrieves recently added products for a business")
    public ResponseEntity<List<ProductResponse>> getRecentlyAddedProducts(
            @Parameter(description = "Business ID") @PathVariable Long businessId,
            @Parameter(description = "Maximum number of products to return") @RequestParam(defaultValue = "10") int limit) {
        log.debug("Getting recently added {} products for business ID: {}", limit, businessId);
        List<ProductResponse> response = productService.getRecentlyAddedProducts(String.valueOf(businessId), limit);
        return ResponseEntity.ok(response);
    }

    // NOTE: Keep all other existing methods (getProductById, updateProduct, deleteProduct, etc.)
    // Only the business-related methods needed type changes from String to Long
}