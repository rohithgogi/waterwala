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

    ProductResponse createProduct(ProductCreateRequest request);
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);
    void deleteProduct(Long id);
    void toggleProductStatus(Long id, boolean isActive);
    void toggleProductAvailability(Long id, boolean isAvailable);

    ProductResponse getProductById(Long id);
    ProductResponse getProductBySku(String sku);
    List<ProductResponse> getProductByBusiness(Long businessId);
    List<ProductResponse> getActiveProductByBusiness(Long businessId);
    List<ProductResponse> getAvailableProductByBusiness(Long businessId);

    List<ProductResponse> getProductByCategory(ProductCategory category);
    List<ProductResponse> getProductByType(ProductType type);
    List<ProductResponse> getProductByBrand(String brand);
    List<ProductResponse> searchProductByName(String name);
    List<ProductResponse> getProductInPriceRange(BigDecimal minPrice,BigDecimal maxPrice);
    List<ProductResponse> getProductByCategoryAndBusiness(ProductCategory category,Long businessId);
    List<ProductResponse> getProductByMultipleCategories(List<ProductCategory> categories);

    Page<ProductResponse> getAllAvailableProducts(Pageable pageable);
    Page<ProductResponse> getProductsByCategory(ProductCategory category, Pageable pageable);
    Page<ProductResponse> getProductsByBusiness(Long businessId, Pageable pageable);
    Page<ProductResponse> searchProducts(String searchTerm, Pageable pageable);

    // Business operations
    long countProductsByBusiness(Long businessId);
    List<ProductResponse> getLatestProducts(int limit);
    List<ProductResponse> getProductsWithLowStock();

    // Inventory operations
    void updateProductStock(Long productId, int quantity);
    void reserveProductStock(Long productId, int quantity);
    void releaseProductStock(Long productId, int quantity);
    boolean isProductInStock(Long productId, int requiredQuantity);

    // Validation operations
    boolean isSkuUnique(String sku);
    boolean isSkuUniqueForUpdate(String sku, Long productId);
    boolean canFulfillOrder(Long productId, int quantity);

    // Business analytics
    List<ProductResponse> getTopSellingProducts(Long businessId, int limit);
    List<ProductResponse> getRecentlyAddedProducts(Long businessId, int limit);
}


