package productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @PostMapping
    @Operation(
            summary = "Create a new product",
            description = "Create a new product with inventory information. Automatically sets up initial inventory tracking."
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

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product",
            description = "Update product information. Cannot update inventory directly - use inventory endpoints.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "SKU already exists for another product")
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product ID") @PathVariable String id,
            @Valid @RequestBody ProductUpdateRequest request) {
        log.info("Updating product with ID: {}", id);
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product",
            description = "Permanently deletes a product with all its inventory data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable String id) {
        log.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Toggle product active status",
            description = "Activates or deactivates a product. Inactive products won't appear in customer searches.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> toggleProductStatus(
            @Parameter(description = "Product ID") @PathVariable String id,
            @Parameter(description = "Active status") @RequestParam boolean isActive) {
        log.info("Toggling product status for ID: {} to {}", id, isActive);
        productService.toggleProductStatus(id, isActive);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Toggle product availability",
            description = "Makes a product available or unavailable for purchase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product availability updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> toggleProductAvailability(
            @Parameter(description = "Product ID") @PathVariable String id,
            @Parameter(description = "Availability status") @RequestParam boolean isAvailable) {
        log.info("Toggling product availability for ID: {} to {}", id, isAvailable);
        productService.toggleProductAvailability(id, isAvailable);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID",
            description = "Retrieves a single product with complete details including inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product ID") @PathVariable String id) {
        log.debug("Getting product by ID: {}", id);
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU",
            description = "Retrieves a single product by its unique SKU")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponse> getProductBySku(
            @Parameter(description = "Product SKU") @PathVariable String sku) {
        log.debug("Getting product by SKU: {}", sku);
        ProductResponse response = productService.getProductBySku(sku);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}")
    @Operation(summary = "Get all products by business",
            description = "Retrieves all products (active and inactive) for a specific business")
    public ResponseEntity<List<ProductResponse>> getProductsByBusiness(
            @Parameter(description = "Business ID") @PathVariable String businessId) {
        log.debug("Getting products by business ID: {}", businessId);
        List<ProductResponse> response = productService.getProductByBusiness(businessId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/active")
    @Operation(summary = "Get active products by business",
            description = "Retrieves only active products for a specific business")
    public ResponseEntity<List<ProductResponse>> getActiveProductsByBusiness(
            @Parameter(description = "Business ID") @PathVariable String businessId) {
        log.debug("Getting active products by business ID: {}", businessId);
        List<ProductResponse> response = productService.getActiveProductByBusiness(businessId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/available")
    @Operation(summary = "Get available products by business",
            description = "Retrieves available products for a specific business")
    public ResponseEntity<List<ProductResponse>> getAvailableProductsByBusiness(
            @Parameter(description = "Business ID") @PathVariable String businessId) {
        log.debug("Getting available products by business ID: {}", businessId);
        List<ProductResponse> response = productService.getAvailableProductByBusiness(businessId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category",
            description = "Retrieves all active and available products in a specific category")
    public ResponseEntity<List<ProductResponse>> getProductByCategory(
            @Parameter(description = "Product category") @PathVariable ProductCategory category) {
        log.debug("Getting products by category: {}", category);
        List<ProductResponse> responses = productService.getProductByCategory(category);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get products by type",
            description = "Retrieves products by type (PRODUCT, SERVICE, SUBSCRIPTION)")
    public ResponseEntity<List<ProductResponse>> getProductsByType(
            @Parameter(description = "Product type") @PathVariable ProductType type) {
        log.debug("Getting products by type: {}", type);
        List<ProductResponse> response = productService.getProductByType(type);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/brand/{brand}")
    @Operation(summary = "Get products by brand",
            description = "Retrieves products by brand name")
    public ResponseEntity<List<ProductResponse>> getProductsByBrand(
            @Parameter(description = "Brand name") @PathVariable String brand) {
        log.debug("Getting products by brand: {}", brand);
        List<ProductResponse> response = productService.getProductByBrand(brand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}/business/{businessId}")
    @Operation(summary = "Get products by category and business",
            description = "Retrieves products by category for a specific business")
    public ResponseEntity<List<ProductResponse>> getProductsByCategoryAndBusiness(
            @Parameter(description = "Product category") @PathVariable ProductCategory category,
            @Parameter(description = "Business ID") @PathVariable String businessId) {
        log.debug("Getting products by category: {} and business ID: {}", category, businessId);
        List<ProductResponse> response = productService.getProductByCategoryAndBusiness(category, businessId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    @Operation(summary = "Get products by multiple categories",
            description = "Retrieves products matching any of the specified categories")
    public ResponseEntity<List<ProductResponse>> getProductsByMultipleCategories(
            @Parameter(description = "List of product categories") @RequestParam List<ProductCategory> categories) {
        log.debug("Getting products by multiple categories: {}", categories);
        List<ProductResponse> response = productService.getProductByMultipleCategories(categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name",
            description = "Searches products by name or description (case insensitive)")
    public ResponseEntity<List<ProductResponse>> searchProductByName(
            @Parameter(description = "Search term") @RequestParam String name) {
        log.debug("Searching products by name: {}", name);
        List<ProductResponse> responses = productService.searchProductByName(name);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/price-range")
    @Operation(summary = "Get products by price range",
            description = "Retrieves products within specified price range")
    public ResponseEntity<List<ProductResponse>> getProductsInPriceRange(
            @Parameter(description = "Minimum price") @RequestParam BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam BigDecimal maxPrice) {
        log.debug("Getting products in price range: {} - {}", minPrice, maxPrice);
        List<ProductResponse> response = productService.getProductInPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all products with pagination",
            description = "Retrieves all available products with pagination and sorting")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    public ResponseEntity<Page<ProductResponse>> getAllAvailableProducts(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDirection) {
        log.debug("Getting all available products - page: {}, size: {}", page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> response = productService.getAllAvailableProducts(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}/page")
    @Operation(summary = "Get products by category with pagination",
            description = "Retrieves products by category with pagination support")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategoryPaginated(
            @Parameter(description = "Product category") @PathVariable ProductCategory category,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDirection) {
        log.debug("Getting products by category: {} - page: {}, size: {}", category, page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> response = productService.getProductsByCategory(category, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/page")
    @Operation(summary = "Get products by business with pagination",
            description = "Retrieves products by business with pagination support")
    public ResponseEntity<Page<ProductResponse>> getProductsByBusinessPaginated(
            @Parameter(description = "Business ID") @PathVariable String businessId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDirection) {
        log.debug("Getting products by business ID: {} - page: {}, size: {}", businessId, page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> response = productService.getProductsByBusiness(businessId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/page")
    @Operation(summary = "Search products with pagination", description = "Searches products with pagination support")
    public ResponseEntity<Page<ProductResponse>> searchProductsPaginated(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDirection) {
        log.debug("Searching products with term: {} - page: {}, size: {}", searchTerm, page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> response = productService.searchProducts(searchTerm, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/count")
    @Operation(summary = "Count products by business", description = "Returns the count of active products for a business")
    public ResponseEntity<Long> countProductsByBusiness(
            @Parameter(description = "Business ID") @PathVariable String businessId) {
        log.debug("Counting products by business ID: {}", businessId);
        long count = productService.countProductsByBusiness(businessId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest products", description = "Retrieves the most recently created products")
    public ResponseEntity<List<ProductResponse>> getLatestProducts(
            @Parameter(description = "Maximum number of products to return") @RequestParam(defaultValue = "10") int limit) {
        log.debug("Getting latest {} products", limit);
        List<ProductResponse> response = productService.getLatestProducts(limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get products with low stock", description = "Retrieves products with stock below their reorder point")
    public ResponseEntity<List<ProductResponse>> getProductsWithLowStock() {
        log.debug("Getting products with low stock");
        List<ProductResponse> response = productService.getProductsWithLowStock();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/top-selling")
    @Operation(summary = "Get top selling products", description = "Retrieves top selling products for a business (based on recent activity)")
    public ResponseEntity<List<ProductResponse>> getTopSellingProducts(
            @Parameter(description = "Business ID") @PathVariable String businessId,
            @Parameter(description = "Maximum number of products to return") @RequestParam(defaultValue = "10") int limit) {
        log.debug("Getting top {} selling products for business ID: {}", limit, businessId);
        List<ProductResponse> response = productService.getTopSellingProducts(businessId, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/recent")
    @Operation(summary = "Get recently added products", description = "Retrieves recently added products for a business")
    public ResponseEntity<List<ProductResponse>> getRecentlyAddedProducts(
            @Parameter(description = "Business ID") @PathVariable String businessId,
            @Parameter(description = "Maximum number of products to return") @RequestParam(defaultValue = "10") int limit) {
        log.debug("Getting recently added {} products for business ID: {}", limit, businessId);
        List<ProductResponse> response = productService.getRecentlyAddedProducts(businessId, limit);
        return ResponseEntity.ok(response);
    }

    // ===== INVENTORY MANAGEMENT =====
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock",
            description = "Updates the current stock of a product (can be positive or negative)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> updateProductStock(
            @Parameter(description = "Product ID") @PathVariable String id,
            @Parameter(description = "Quantity to add/subtract from current stock") @RequestParam int quantity) {
        log.info("Updating stock for product ID: {} by quantity: {}", id, quantity);
        productService.updateProductStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reserve-stock")
    @Operation(summary = "Reserve product stock",
            description = "Reserves stock for a product (moves from current to reserved stock)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock reserved successfully"),
            @ApiResponse(responseCode = "400", description = "Insufficient stock available"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> reserveProductStock(
            @Parameter(description = "Product ID") @PathVariable String id,
            @Parameter(description = "Quantity to reserve") @RequestParam int quantity) {
        log.info("Reserving stock for product ID: {} quantity: {}", id, quantity);
        productService.reserveProductStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/release-stock")
    @Operation(summary = "Release reserved stock",
            description = "Releases reserved stock back to current stock (e.g., when order is cancelled)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock released successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> releaseProductStock(
            @Parameter(description = "Product ID") @PathVariable String id,
            @Parameter(description = "Quantity to release from reserved stock") @RequestParam int quantity) {
        log.info("Releasing reserved stock for product ID: {} quantity: {}", id, quantity);
        productService.releaseProductStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/stock-check")
    @Operation(summary = "Check if product is in stock",
            description = "Checks if a product has sufficient current stock for the required quantity")
    public ResponseEntity<Boolean> isProductInStock(
            @Parameter(description = "Product ID") @PathVariable String id,
            @Parameter(description = "Required quantity") @RequestParam int requiredQuantity) {
        log.debug("Checking if product ID: {} has sufficient stock for quantity: {}", id, requiredQuantity);
        boolean inStock = productService.isProductInStock(id, requiredQuantity);
        return ResponseEntity.ok(inStock);
    }


    @GetMapping("/validate/sku/{sku}")
    @Operation(summary = "Check if SKU is unique",
            description = "Validates if a SKU is unique across all products")
    public ResponseEntity<Boolean> isSkuUnique(
            @Parameter(description = "SKU to check") @PathVariable String sku) {
        log.debug("Checking if SKU is unique: {}", sku);
        boolean isUnique = productService.isSkuUnique(sku);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/validate/sku/{sku}/exclude/{productId}")
    @Operation(summary = "Check if SKU is unique for update",
            description = "Validates if a SKU is unique excluding a specific product (for updates)")
    public ResponseEntity<Boolean> isSkuUniqueForUpdate(
            @Parameter(description = "SKU to check") @PathVariable String sku,
            @Parameter(description = "Product ID to exclude from check") @PathVariable String productId) {
        log.debug("Checking if SKU is unique for update: {} excluding product ID: {}", sku, productId);
        boolean isUnique = productService.isSkuUniqueForUpdate(sku, productId);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/{id}/can-fulfill/{quantity}")
    @Operation(summary = "Check if order can be fulfilled",
            description = "Checks if an order can be fulfilled for a product with the specified quantity")
    public ResponseEntity<Boolean> canFulfillOrder(
            @Parameter(description = "Product ID") @PathVariable String id,
            @Parameter(description = "Order quantity") @PathVariable int quantity) {
        log.debug("Checking if order can be fulfilled for product ID: {} quantity: {}", id, quantity);
        boolean canFulfill = productService.canFulfillOrder(id, quantity);
        return ResponseEntity.ok(canFulfill);
    }
}