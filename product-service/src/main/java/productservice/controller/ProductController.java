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
import productservice.dto.ProductCreateRequest;
import productservice.dto.ProductResponse;
import productservice.dto.ProductUpdateRequest;
import productservice.model.ProductCategory;
import productservice.model.ProductType;
import productservice.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create a new product", description = "Create a new product with inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Product withSKU already exists")
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        log.info("Creating product with SKU: {}", request.getSku());
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product", description = "Update product information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "SKU already exists for another product")
    })
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
        log.info("Update product with ID: {}", id);

        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Permenantly deletes a product with inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("Deleting a product with id: {}", id);

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Toggle product active status", description = "Activates or deactivates a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> toggleProductStatus(
            @PathVariable Long id,
            @RequestParam boolean isActive) {
        log.info("Toggling product status for ID: {} to {}", id, isActive);

        productService.toggleProductStatus(id, isActive);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Toggle product availability", description = "Makes a product available or unavailable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product availability updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> toggleProductAvailability(@PathVariable Long id, @RequestParam boolean isAvailable){
        log.info("Toggling product availability for id:{} to {}",id,isAvailable);

        productService.toggleProductAvailability(id,isAvailable);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a single product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.debug("Getting product by ID: {}", id);

        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU", description = "Retrieves a single product by its SKU")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku){
        log.debug("Getting product using SKU: {}",sku);
        ProductResponse response = productService.getProductBySku(sku);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/business/{businessId}")
    @Operation(summary = "Get all products by business", description = "Retrieves all products for a specific business")
    public ResponseEntity<List<ProductResponse>> getProductsByBusiness(@PathVariable Long businessId) {
        log.debug("Getting products by business ID: {}", businessId);

        List<ProductResponse> response = productService.getProductByBusiness(businessId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/active")
    @Operation(summary = "Get active products by business", description = "Retrieves active products for a specific business")
    public ResponseEntity<List<ProductResponse>> getActiveProductsByBusiness(@PathVariable Long businessId) {
        log.debug("Getting active products by business ID: {}", businessId);

        List<ProductResponse> response = productService.getActiveProductByBusiness(businessId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/available")
    @Operation(summary = "Get available products by business", description = "Retrieves available products for a specific business")
    public ResponseEntity<List<ProductResponse>> getAvailableProductsByBusiness(@PathVariable Long businessId) {
        log.debug("Getting available products by business ID: {}", businessId);

        List<ProductResponse> response = productService.getAvailableProductByBusiness(businessId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category", description = "Retrieves products by category")
    public ResponseEntity<List<ProductResponse>> getProductByCategory(@PathVariable ProductCategory category){
        log.debug("Getting products by category: {}",category);

        List<ProductResponse> responses=productService.getProductByCategory(category);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get products by type", description = "Retrieves products by type")
    public ResponseEntity<List<ProductResponse>> getProductsByType(
            @PathVariable ProductType type) {
        log.debug("Getting products by type: {}", type);

        List<ProductResponse> response = productService.getProductByType(type);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/brand/{brand}")
    @Operation(summary = "Get products by brand", description = "Retrieves products by brand")
    public ResponseEntity<List<ProductResponse>> getProductsByBrand(@PathVariable String brand) {
        log.debug("Getting products by brand: {}", brand);

        List<ProductResponse> response = productService.getProductByBrand(brand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}/business/{businessId}")
    @Operation(summary = "Get products by category and business", description = "Retrieves products by category for a specific business")
    public ResponseEntity<List<ProductResponse>> getProductsByCategoryAndBusiness(
            @PathVariable ProductCategory category,
            @PathVariable Long businessId) {
        log.debug("Getting products by category: {} and business ID: {}", category, businessId);

        List<ProductResponse> response = productService.getProductByCategoryAndBusiness(category, businessId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    @Operation(summary = "Get products by multiple categories", description = "Retrieves products by multiple categories")
    public ResponseEntity<List<ProductResponse>> getProductsByMultipleCategories(
            @RequestParam List<ProductCategory> categories) {
        log.debug("Getting products by multiple categories: {}", categories);

        List<ProductResponse> response = productService.getProductByMultipleCategories(categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name", description = "Searches products by name (case insensitive)")
    public ResponseEntity<List<ProductResponse>> searchProductByName(@RequestParam String name){
        log.debug("Search products by name: {}", name);

        List<ProductResponse> responses=productService.searchProductByName(name);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/price-range")
    @Operation(summary = "Get products by price range", description = "Retrieves products within a price range")
    public ResponseEntity<List<ProductResponse>> getProductsInPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        log.debug("Getting products in price range: {} - {}", minPrice, maxPrice);

        List<ProductResponse> response = productService.getProductInPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all products available with pagination", description = "Retrieves all available products with pagination")
    public ResponseEntity<Page<ProductResponse>> getAllAvailableProducts(
            @Parameter(description = "Page number(0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDirection){
        log.debug("Getting all available products - page: {}, size: {}", page, size);
        Sort sort=Sort.by(Sort.Direction.fromString(sortDirection),sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponse> response = productService.getAllAvailableProducts(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}/page")
    @Operation(summary = "Get products by category with pagination", description = "Retrieves products by category with pagination support")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategoryPaginated(
            @PathVariable ProductCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        log.debug("Getting products by category: {} - page: {}, size: {}", category, page, size);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponse> response = productService.getProductsByCategory(category, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/page")
    @Operation(summary = "Get products by business with pagination", description = "Retrieves products by business with pagination support")
    public ResponseEntity<Page<ProductResponse>> getProductsByBusinessPaginated(
            @PathVariable Long businessId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        log.debug("Getting products by business ID: {} - page: {}, size: {}", businessId, page, size);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponse> response = productService.getProductsByBusiness(businessId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/page")
    @Operation(summary = "Search products with pagination", description = "Searches products with pagination support")
    public ResponseEntity<Page<ProductResponse>> searchProductsPaginated(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        log.debug("Searching products with term: {} - page: {}, size: {}", searchTerm, page, size);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponse> response = productService.searchProducts(searchTerm, pageable);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/business/{businessId}/count")
    @Operation(summary = "Count products by business", description = "Returns the count of products for a business")
    public ResponseEntity<Long> countProductsByBusiness(@PathVariable Long businessId) {
        log.debug("Counting products by business ID: {}", businessId);

        long count = productService.countProductsByBusiness(businessId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest products", description = "Retrieves the latest products")
    public ResponseEntity<List<ProductResponse>> getLatestProducts(
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("Getting latest {} products", limit);

        List<ProductResponse> response = productService.getLatestProducts(limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get products with low stock", description = "Retrieves products with stock below reorder point")
    public ResponseEntity<List<ProductResponse>> getProductsWithLowStock() {
        log.debug("Getting products with low stock");

        List<ProductResponse> response = productService.getProductsWithLowStock();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/top-selling")
    @Operation(summary = "Get top selling products", description = "Retrieves top selling products for a business")
    public ResponseEntity<List<ProductResponse>> getTopSellingProducts(
            @PathVariable Long businessId,
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("Getting top {} selling products for business ID: {}", limit, businessId);

        List<ProductResponse> response = productService.getTopSellingProducts(businessId, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/recent")
    @Operation(summary = "Get recently added products", description = "Retrieves recently added products for a business")
    public ResponseEntity<List<ProductResponse>> getRecentlyAddedProducts(
            @PathVariable Long businessId,
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("Getting recently added {} products for business ID: {}", limit, businessId);

        List<ProductResponse> response = productService.getRecentlyAddedProducts(businessId, limit);
        return ResponseEntity.ok(response);
    }

    //INVENTORY MANAGEMENT
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock", description = "Updates the current stock of a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> updateProductStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        log.info("Updating stock for product ID: {} by quantity: {}", id, quantity);

        productService.updateProductStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reserve-stock")
    @Operation(summary = "Reserve product stock", description = "Reserves stock for a product (moves from current to reserved)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock reserved successfully"),
            @ApiResponse(responseCode = "400", description = "Insufficient stock"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> reserveProductStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        log.info("Reserving stock for product ID: {} quantity: {}", id, quantity);

        productService.reserveProductStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/release-stock")
    @Operation(summary = "Release reserved stock", description = "Releases reserved stock back to current stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock released successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> releaseProductStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        log.info("Releasing reserved stock for product ID: {} quantity: {}", id, quantity);

        productService.releaseProductStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/stock-check")
    @Operation(summary = "Check if product is in stock", description = "Checks if a product has sufficient stock")
    public ResponseEntity<Boolean> isProductInStock(
            @PathVariable Long id,
            @RequestParam int requiredQuantity) {
        log.debug("Checking if product ID: {} has sufficient stock for quantity: {}", id, requiredQuantity);

        boolean inStock = productService.isProductInStock(id, requiredQuantity);
        return ResponseEntity.ok(inStock);
    }

    //VALIDATION ENDPOINTS
    @GetMapping("/validate/sku/{sku}")
    @Operation(summary = "Check if SKU is unique", description = "Validates if a SKU is unique across all products")
    public ResponseEntity<Boolean> isSkuUnique(@PathVariable String sku) {
        log.debug("Checking if SKU is unique: {}", sku);

        boolean isUnique = productService.isSkuUnique(sku);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/validate/sku/{sku}/exclude/{productId}")
    @Operation(summary = "Check if SKU is unique for update", description = "Validates if a SKU is unique excluding a specific product")
    public ResponseEntity<Boolean> isSkuUniqueForUpdate(
            @PathVariable String sku,
            @PathVariable Long productId) {
        log.debug("Checking if SKU is unique for update: {} excluding product ID: {}", sku, productId);

        boolean isUnique = productService.isSkuUniqueForUpdate(sku, productId);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/{id}/can-fulfill/{quantity}")
    @Operation(summary = "Check if order can be fulfilled", description = "Checks if an order can be fulfilled for a product")
    public ResponseEntity<Boolean> canFulfillOrder(
            @PathVariable Long id,
            @PathVariable int quantity) {
        log.debug("Checking if order can be fulfilled for product ID: {} quantity: {}", id, quantity);

        boolean canFulfill = productService.canFulfillOrder(id, quantity);
        return ResponseEntity.ok(canFulfill);
    }

}
