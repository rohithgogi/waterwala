// ProductRepository.java
package productservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import productservice.model.Product;
import productservice.model.ProductCategory;
import productservice.model.ProductType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByBusinessId(String businessId);

    List<Product> findByBusinessIdAndIsActiveTrueAndIsAvailableTrue(String businessId);

    List<Product> findByCategoryAndIsActiveTrueAndIsAvailableTrue(ProductCategory category);

    List<Product> findByTypeAndIsActiveTrueAndIsAvailableTrue(ProductType type);

    Optional<Product> findBySku(String sku);

    List<Product> findByNameRegexAndIsActiveTrueAndIsAvailableTrue(String nameRegex);

    // Products in price range
    @Query("{ 'isActive': true, 'isAvailable': true, 'basePrice': { $gte: ?0, $lte: ?1 } }")
    List<Product> findProductsInPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    // Find products by category and business
    List<Product> findByCategoryAndBusinessIdAndIsActiveTrueAndIsAvailableTrue(
            ProductCategory category, String businessId);

    // Pagination queries
    Page<Product> findByIsActiveTrueAndIsAvailableTrue(Pageable pageable);

    Page<Product> findByCategoryAndIsActiveTrueAndIsAvailableTrue(
            ProductCategory category, Pageable pageable);

    Page<Product> findByBusinessIdAndIsActiveTrueAndIsAvailableTrue(
            String businessId, Pageable pageable);

    // Search products by name and description
    @Query("{ 'isActive': true, 'isAvailable': true, " +
            "$or: [" +
            "  { 'name': { $regex: ?0, $options: 'i' } }," +
            "  { 'description': { $regex: ?0, $options: 'i' } }" +
            "] }")
    List<Product> searchAvailableActiveProducts(String searchTerm);

    // Count products by business
    long countByBusinessIdAndIsActiveTrue(String businessId);

    // Find products by brand
    List<Product> findByBrandAndIsActiveTrueAndIsAvailableTrue(String brand);

    // Find products with low stock
    @Query("{ 'isActive': true, 'inventory.currentStock': { $lte: '$inventory.reorderPoint' } }")
    List<Product> findProductsWithLowStock();

    // Check if SKU exists for different product
    boolean existsBySkuAndIdNot(String sku, String id);

    // Find products by multiple categories
    @Query("{ 'isActive': true, 'isAvailable': true, 'category': { $in: ?0 } }")
    List<Product> findByMultipleCategories(List<ProductCategory> categories);

    // Find latest products
    @Query(value = "{ 'isActive': true, 'isAvailable': true }", sort = "{ 'createdAt': -1 }")
    List<Product> findLatestProducts(Pageable pageable);

    // Check if product has sufficient stock
    @Query("{ '_id': ?0, 'inventory.currentStock': { $gte: ?1 } }")
    Optional<Product> findByIdWithSufficientStock(String productId, Integer requiredQuantity);

    // Find products by business with stock below reorder point
    @Query("{ 'businessId': ?0, 'isActive': true, " +
            "'inventory.currentStock': { $lte: '$inventory.reorderPoint' } }")
    List<Product> findLowStockProductsByBusiness(String businessId);

    // Aggregation for top selling products (placeholder - would need sales data)
    @Query(value = "{ 'businessId': ?0, 'isActive': true, 'isAvailable': true }",
            sort = "{ 'createdAt': -1 }")
    List<Product> findTopSellingProductsByBusiness(String businessId, Pageable pageable);

    // Text search across multiple fields
    @Query("{ $text: { $search: ?0 }, 'isActive': true, 'isAvailable': true }")
    List<Product> findByTextSearch(String searchText);
}