package productservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import productservice.model.Product;
import productservice.model.ProductCategory;
import productservice.model.ProductType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByBusinessId(Long businessId);

    List<Product> findByBusinessIdAndIsActiveTrue(Long businessId);

    List<Product> findByBusinessIdAndIsActiveTrueAndIsAvailableTrue(Long businessId);

    List<Product> findByCategoryAndIsActiveTrueAndIsAvailableTrue(ProductCategory category);

    List<Product> findByTypeAndIsActiveTrueAndIsAvailableTrue(ProductType type);

    Optional<Product> findBySku(String sku);

    List<Product> findByNameContainingIgnoreCaseAndIsActiveTrueAndIsAvailableTrue(String name);

    //products in price range
    @Query("SELECT p FROM Product p WHERE p.isActive=true AND p.isAvailable=true "+
    "AND p.basePrice BETWEEN :minPrice AND :maxPrice")
    List<Product> findProductsInPriceRange(@Param("minPrice")BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    //find products by category and business
    List<Product> findByCategoryAndBusinessIdAndIsActiveTrueAndIsAvailableTrue(ProductCategory category, Long businessId);

    //pagination
    Page<Product> findByIsActiveTrueAndIsAvailableTrue(Pageable pageable);

    //find products by category with pagination
    Page<Product> findByCategoryAndIsActiveTrueAndIsAvailableTrue(ProductCategory category, Pageable pageable);

    //Search products by name and description
    @Query("SELECT p FROM Product p WHERE p.isActive=true AND p.isAvailable=true "+
            "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "+
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Product> searchAvailableActiveProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    //find products by business with pagination
    Page<Product> findByBusinessIdAndIsActiveTrueAndIsAvailableTrue(Long businessId, Pageable pageable);

    //count products by business
    long countByBusinessIdAndIsActiveTrue(Long businessId);

    //find products by brand
    List<Product> findByBrandAndIsActiveTrueAndIsAvailableTrue(String brand);

    //find products with low stock
    @Query("SELECT p FROM Product p JOIN ProductInventory pi ON p.id=pi.product.id"+
        " WHERE p.isActive=true AND pi.currentStock <= pi.reorderPoint")
    List<Product> findProductsWithLowStock();

    // Check if SKU exists for different product
    boolean existsBySkuAndIdNot(String sku, Long id);

    // Find products by multiple categories
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isAvailable = true " +
            "AND p.category IN :categories")
    List<Product> findByMultipleCategories(@Param("categories") List<ProductCategory> categories);

    // Find featured products (could be based on criteria like high rating, popular, etc.)
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isAvailable = true " +
            "ORDER BY p.createdAt DESC")
    List<Product> findLatestProducts(Pageable pageable);


}
