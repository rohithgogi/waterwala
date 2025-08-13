package productservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import productservice.dto.ProductCreateRequest;
import productservice.dto.ProductResponse;
import productservice.dto.ProductUpdateRequest;
import productservice.model.ProductCategory;
import productservice.model.ProductType;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    // Basic CRUD operations
    ProductResponse createProduct(ProductCreateRequest request);
    ProductResponse updateProduct(String id, ProductUpdateRequest request);
    void deleteProduct(String id);
    void toggleProductStatus(String id, boolean isActive);
    void toggleProductAvailability(String id, boolean isAvailable);

    // Retrieval operations
    ProductResponse getProductById(String id);
    ProductResponse getProductBySku(String sku);
    List<ProductResponse> getProductByBusiness(String businessId);
    List<ProductResponse> getActiveProductByBusiness(String businessId);
    List<ProductResponse> getAvailableProductByBusiness(String businessId);

    // Category and type operations
    List<ProductResponse> getProductByCategory(ProductCategory category);
    List<ProductResponse> getProductByType(ProductType type);
    List<ProductResponse> getProductByBrand(String brand);
    List<ProductResponse> searchProductByName(String name);
    List<ProductResponse> getProductInPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<ProductResponse> getProductByCategoryAndBusiness(ProductCategory category, String businessId);
    List<ProductResponse> getProductByMultipleCategories(List<ProductCategory> categories);

    // Pagination operations
    Page<ProductResponse> getAllAvailableProducts(Pageable pageable);
    Page<ProductResponse> getProductsByCategory(ProductCategory category, Pageable pageable);
    Page<ProductResponse> getProductsByBusiness(String businessId, Pageable pageable);
    Page<ProductResponse> searchProducts(String searchTerm, Pageable pageable);

    // Business operations
    long countProductsByBusiness(String businessId);
    List<ProductResponse> getLatestProducts(int limit);
    List<ProductResponse> getProductsWithLowStock();

    // Inventory operations
    void updateProductStock(String productId, int quantity);
    void reserveProductStock(String productId, int quantity);
    void releaseProductStock(String productId, int quantity);
    boolean isProductInStock(String productId, int requiredQuantity);

    // Validation operations
    boolean isSkuUnique(String sku);
    boolean isSkuUniqueForUpdate(String sku, String productId);
    boolean canFulfillOrder(String productId, int quantity);

    // Business analytics
    List<ProductResponse> getTopSellingProducts(String businessId, int limit);
    List<ProductResponse> getRecentlyAddedProducts(String businessId, int limit);
}