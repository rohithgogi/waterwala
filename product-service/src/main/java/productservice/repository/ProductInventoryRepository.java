package productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import productservice.model.ProductInventory;

import java.util.List;
import java.util.Optional;

public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
    Optional<ProductInventory> findProductById(Long id);

    @Query("SELECT pi FROM ProductInventory pi WHERE pi.currentStock <= pi.reorderPoint")
    List<ProductInventory> findLowStockInventories();

    List<ProductInventory> findByWarehouseLocation(String warehouseLocation);

    // Find inventories with stock below minimum level
    @Query("SELECT pi FROM ProductInventory pi WHERE pi.currentStock < pi.minStockLevel")
    List<ProductInventory> findInventoriesBelowMinLevel();

    // Find inventories with stock above maximum level
    @Query("SELECT pi FROM ProductInventory pi WHERE pi.currentStock > pi.maxStockLevel")
    List<ProductInventory> findInventoriesAboveMaxLevel();

    // Find inventories with reserved stock
    @Query("SELECT pi FROM ProductInventory pi WHERE pi.reservedStock > 0")
    List<ProductInventory> findInventoriesWithReservedStock();

    // Get total stock for a product (current + reserved)
    @Query("SELECT (pi.currentStock + pi.reservedStock) FROM ProductInventory pi WHERE pi.product.id = :productId")
    Integer getTotalStockByProductId(@Param("productId") Long productId);

    // Check if product has sufficient stock
    @Query("SELECT CASE WHEN pi.currentStock >= :requiredQuantity THEN true ELSE false END " +
            "FROM ProductInventory pi WHERE pi.product.id = :productId")
    Boolean hasSufficientStock(@Param("productId") Long productId, @Param("requiredQuantity") Integer requiredQuantity);

    // Update current stock
    @Query("UPDATE ProductInventory pi SET pi.currentStock = pi.currentStock + :quantity WHERE pi.product.id = :productId")
    void updateCurrentStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    // Update reserved stock
    @Query("UPDATE ProductInventory pi SET pi.reservedStock = pi.reservedStock + :quantity WHERE pi.product.id = :productId")
    void updateReservedStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    // Find inventories by business ID (through product relationship)
    @Query("SELECT pi FROM ProductInventory pi JOIN pi.product p WHERE p.businessId = :businessId")
    List<ProductInventory> findByBusinessId(@Param("businessId") Long businessId);

    // Get inventory summary statistics
    @Query("SELECT COUNT(pi), SUM(pi.currentStock), SUM(pi.reservedStock) " +
            "FROM ProductInventory pi JOIN pi.product p WHERE p.businessId = :businessId")
    Object[] getInventoryStatsByBusinessId(@Param("businessId") Long businessId);

    // Delete inventory by product ID
    void deleteByProductId(Long productId);

    // Check if inventory exists for product
    boolean existsByProductId(Long productId);
}
