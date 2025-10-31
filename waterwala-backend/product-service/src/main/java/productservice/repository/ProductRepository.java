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

    // CHANGED: All businessId parameters from String to Long
    List<Product> findByBusinessId(Long businessId);

    List<Product> findByBusinessIdAndIsActiveTrueAndIsAvailableTrue(Long businessId);

    List<Product> findByCategoryAndIsActiveTrueAndIsAvailableTrue(ProductCategory category);

    List<Product> findByTypeAndIsActiveTrueAndIsAvailableTrue(ProductType type);

    Optional<Product> findBySku(String sku);

    List<Product> findByNameRegexAndIsActiveTrueAndIsAvailableTrue(String nameRegex);

    @Query("{ 'isActive': true, 'isAvailable': true, 'basePrice': { $gte: ?0, $lte: ?1 } }")
    List<Product> findProductsInPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByCategoryAndBusinessIdAndIsActiveTrueAndIsAvailableTrue(
            ProductCategory category, Long businessId);

    Page<Product> findByIsActiveTrueAndIsAvailableTrue(Pageable pageable);

    Page<Product> findByCategoryAndIsActiveTrueAndIsAvailableTrue(
            ProductCategory category, Pageable pageable);

    Page<Product> findByBusinessIdAndIsActiveTrueAndIsAvailableTrue(
            Long businessId, Pageable pageable);

    @Query("{ 'isActive': true, 'isAvailable': true, " +
            "$or: [" +
            "  { 'name': { $regex: ?0, $options: 'i' } }," +
            "  { 'description': { $regex: ?0, $options: 'i' } }" +
            "] }")
    List<Product> searchAvailableActiveProducts(String searchTerm);

    long countByBusinessIdAndIsActiveTrue(Long businessId);

    List<Product> findByBrandAndIsActiveTrueAndIsAvailableTrue(String brand);

    @Query("{ 'isActive': true, 'inventory.currentStock': { $lte: '$inventory.reorderPoint' } }")
    List<Product> findProductsWithLowStock();

    boolean existsBySkuAndIdNot(String sku, String id);

    @Query("{ 'isActive': true, 'isAvailable': true, 'category': { $in: ?0 } }")
    List<Product> findByMultipleCategories(List<ProductCategory> categories);

    @Query(value = "{ 'isActive': true, 'isAvailable': true }", sort = "{ 'createdAt': -1 }")
    List<Product> findLatestProducts(Pageable pageable);

    @Query("{ '_id': ?0, 'inventory.currentStock': { $gte: ?1 } }")
    Optional<Product> findByIdWithSufficientStock(String productId, Integer requiredQuantity);

    @Query("{ 'businessId': ?0, 'isActive': true, " +
            "'inventory.currentStock': { $lte: '$inventory.reorderPoint' } }")
    List<Product> findLowStockProductsByBusiness(Long businessId);

    @Query(value = "{ 'businessId': ?0, 'isActive': true, 'isAvailable': true }",
            sort = "{ 'createdAt': -1 }")
    List<Product> findTopSellingProductsByBusiness(Long businessId, Pageable pageable);

    @Query("{ $text: { $search: ?0 }, 'isActive': true, 'isAvailable': true }")
    List<Product> findByTextSearch(String searchText);
}