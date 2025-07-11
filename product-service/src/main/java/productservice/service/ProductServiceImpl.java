package productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productservice.dto.ProductCreateRequest;
import productservice.dto.ProductResponse;
import productservice.dto.ProductUpdateRequest;
import productservice.exceptions.*;
import productservice.mapper.ProductMapper;
import productservice.model.Product;
import productservice.model.ProductCategory;
import productservice.model.ProductInventory;
import productservice.model.ProductType;
import productservice.repository.ProductInventoryRepository;
import productservice.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final ProductInventoryRepository inventoryRepository;
    private final ProductMapper productMapper;

    public ProductResponse createProduct(ProductCreateRequest request){
        log.info("Creating product with SKU: {}",request.getSku());

        if(productRepository.findBySku(request.getSku()).isPresent()){
            throw new DuplicateSkuException(request.getSku());
        }
        validateProductData(request);

        try{
            Product product=productMapper.toEntity(request);
            Product savedProduct=productRepository.save(product);

            ProductInventory inventory=createInventoryRecord(savedProduct, request);
            inventoryRepository.save(inventory);

            log.info("Product created successfully with ID: {}",savedProduct.getId());
            return productMapper.toResponseWithInventory(savedProduct, inventory);
        }catch(Exception e){
            log.error("Error creating product: {}", e.getMessage(), e);
            throw new ProductServiceException("Failed to create product", e);
        }
    }

    public ProductResponse updateProduct(Long id, ProductUpdateRequest request){
        log.info("Updating product with ID: {}", id);

        Product product = findProductById(id);

        // Validate SKU uniqueness for update
        if (!isSkuUniqueForUpdate(request.getSku(), id)) {
            throw new DuplicateSkuException(request.getSku());
        }

        // Validate business logic
        validateProductUpdateData(request);
        try {
            // Update product entity
            productMapper.updateEntity(product, request);
            Product updatedProduct = productRepository.save(product);

            // Get inventory for response
            Optional<ProductInventory> inventory = inventoryRepository.findProductById(id);

            log.info("Product updated successfully with ID: {}", id);
            return productMapper.toResponseWithInventory(updatedProduct, inventory.orElse(null));

        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage(), e);
            throw new ProductServiceException("Failed to update product", e);
        }
    }

    public void deleteProduct(Long id){
        log.info("Deleting product with ID: {}", id);
        Product product=findProductById(id);

        try{

            //first delete inventory(foreign key constraint)
            inventoryRepository.deleteByProductId(id);
            productRepository.delete(product);
            log.info("Product deleted successfully with ID: {}", id);
        }catch(Exception e){
            log.error("Error deleting product: {}",e.getMessage(),e);
            throw new ProductServiceException("Failed to delete product", e);
        }
    }

    @Override
    public void toggleProductStatus(Long id, boolean isActive) {
        log.info("Toggling product status for ID: {} to {}", id, isActive);

        Product product = findProductById(id);
        product.setIsActive(isActive);
        productRepository.save(product);

        log.info("Product status updated successfully for ID: {}", id);
    }

    @Override
    public void toggleProductAvailability(Long id, boolean isAvailable) {
        log.info("Toggling product availability for ID: {} to {}", id, isAvailable);

        Product product = findProductById(id);
        product.setIsAvailable(isAvailable);
        productRepository.save(product);

        log.info("Product availability updated successfully for ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.debug("Getting product by ID: {}", id);

        Product product = findProductById(id);
        Optional<ProductInventory> inventory = inventoryRepository.findProductById(id);

        return productMapper.toResponseWithInventory(product, inventory.orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        log.debug("Getting product by SKU: {}", sku);

        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));

        Optional<ProductInventory> inventory = inventoryRepository.findProductById(product.getId());

        return productMapper.toResponseWithInventory(product, inventory.orElse(null));
    }

    @Override
    @Transactional
    public List<ProductResponse> getProductByBusiness(Long businessId){
        log.debug("Getting products by businessId: {}", businessId);

        List<Product> products=productRepository.findByBusinessId(businessId);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProductByBusiness(Long businessId) {
        log.debug("Getting active products by business ID: {}", businessId);

        List<Product> products = productRepository.findByBusinessIdAndIsActiveTrue(businessId);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAvailableProductByBusiness(Long businessId) {
        log.debug("Getting available products by business ID: {}", businessId);

        List<Product> products = productRepository.findByBusinessIdAndIsActiveTrueAndIsAvailableTrue(businessId);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductByCategory(ProductCategory category) {
        log.debug("Getting products by category: {}", category);

        List<Product> products = productRepository.findByCategoryAndIsActiveTrueAndIsAvailableTrue(category);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductByType(ProductType type) {
        log.debug("Getting products by type: {}", type);

        List<Product> products = productRepository.findByTypeAndIsActiveTrueAndIsAvailableTrue(type);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductByBrand(String brand) {
        log.debug("Getting products by brand: {}", brand);

        List<Product> products = productRepository.findByBrandAndIsActiveTrueAndIsAvailableTrue(brand);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductByName(String name) {
        log.debug("Searching products by name: {}", name);

        List<Product> products = productRepository.findByNameContainingIgnoreCaseAndIsActiveTrueAndIsAvailableTrue(name);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductInPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Getting products in price range: {} - {}", minPrice, maxPrice);

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new InvalidProductDataException("Minimum price cannot be greater than maximum price");
        }

        List<Product> products = productRepository.findProductsInPriceRange(minPrice, maxPrice);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductByCategoryAndBusiness(ProductCategory category, Long businessId) {
        log.debug("Getting products by category: {} and business ID: {}", category, businessId);

        List<Product> products = productRepository.findByCategoryAndBusinessIdAndIsActiveTrueAndIsAvailableTrue(category, businessId);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductByMultipleCategories(List<ProductCategory> categories) {
        log.debug("Getting products by multiple categories: {}", categories);

        if (categories == null || categories.isEmpty()) {
            throw new InvalidProductDataException("Categories list cannot be empty");
        }

        List<Product> products = productRepository.findByMultipleCategories(categories);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllAvailableProducts(Pageable pageable) {
        log.debug("Getting all available products with pagination");

        Page<Product> products = productRepository.findByIsActiveTrueAndIsAvailableTrue(pageable);
        return products.map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(ProductCategory category, Pageable pageable) {
        log.debug("Getting products by category: {} with pagination", category);

        Page<Product> products = productRepository.findByCategoryAndIsActiveTrueAndIsAvailableTrue(category, pageable);
        return products.map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByBusiness(Long businessId, Pageable pageable) {
        log.debug("Getting products by business ID: {} with pagination", businessId);

        Page<Product> products = productRepository.findByBusinessIdAndIsActiveTrueAndIsAvailableTrue(businessId, pageable);
        return products.map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String searchTerm, Pageable pageable) {
        log.debug("Searching products with term: {}", searchTerm);

        List<Product> products = productRepository.searchAvailableActiveProducts(searchTerm, pageable);
        return Page.empty(); // Note: This method in repository returns List, not Page
    }

    @Override
    @Transactional(readOnly = true)
    public long countProductsByBusiness(Long businessId) {
        log.debug("Counting products by business ID: {}", businessId);

        return productRepository.countByBusinessIdAndIsActiveTrue(businessId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getLatestProducts(int limit) {
        log.debug("Getting latest {} products", limit);

        Pageable pageable = PageRequest.of(0, limit);
        List<Product> products = productRepository.findLatestProducts(pageable);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsWithLowStock() {
        log.debug("Getting products with low stock");

        List<Product> products = productRepository.findProductsWithLowStock();
        return productMapper.toResponseList(products);
    }

    @Override
    public void updateProductStock(Long productId, int quantity) {
        log.info("Updating stock for product ID: {} by quantity: {}", productId, quantity);

        if (!inventoryRepository.existsByProductId(productId)) {
            throw new ProductNotFoundException("Product inventory not found for ID: " + productId);
        }

        try {
            inventoryRepository.updateCurrentStock(productId, quantity);
            log.info("Stock updated successfully for product ID: {}", productId);

        } catch (Exception e) {
            log.error("Error updating stock: {}", e.getMessage(), e);
            throw new ProductServiceException("Failed to update stock", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void reserveProductStock(Long productId, int quantity){
        log.info("Reserving stock for productId {} quantity {}",productId,quantity);

        if(!canFulfillOrder(productId,quantity)){
            ProductInventory inventory=inventoryRepository.findProductById(productId)
                    .orElseThrow(()->new ProductNotFoundException("Product inventory not found for ID: " + productId));
            throw new InsufficientStockException(productId,quantity,inventory.getCurrentStock());
        }
         try{
             inventoryRepository.updateCurrentStock(productId,-quantity);
             inventoryRepository.updateReservedStock(productId,quantity);

             log.info("Stock reserved successfully for productId: {}",productId);
         }catch (Exception e){
             log.error("Error reserving stock: {}",e.getMessage(), e);
             throw  new ProductServiceException("Failed to reserve stock", e);
         }

    }

    public void releaseProductStock(Long productId, int quantity){
        log.info("Releasing product stock for productId: {} quantity: {}",productId, quantity);

        try{
            inventoryRepository.updateReservedStock(productId,-quantity);
            inventoryRepository.updateCurrentStock(productId,quantity);
            log.info("Stock released successfully for productId: {}",productId);
        }catch (Exception e){
            log.error("Error in releasing stock: {}",e.getMessage(),e);
            throw new ProductServiceException("Failed to release stock", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInStock(Long productId, int requiredQuantity) {
        log.debug("Checking if product ID: {} has sufficient stock for quantity: {}", productId, requiredQuantity);
        return inventoryRepository.hasSufficientStock(productId, requiredQuantity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSkuUnique(String sku) {
        log.debug("Checking if SKU is unique: {}", sku);

        return productRepository.findBySku(sku).isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSkuUniqueForUpdate(String sku, Long productId) {
        log.debug("Checking if SKU is unique for update: {} excluding product ID: {}", sku, productId);

        return !productRepository.existsBySkuAndIdNot(sku, productId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getTopSellingProducts(Long businessId, int limit) {
        log.debug("Getting top {} selling products for business ID: {}", limit, businessId);

        // This would typically involve sales data, but for now return latest products
        Pageable pageable = PageRequest.of(0, limit);
        List<Product> products = productRepository.findLatestProducts(pageable);
        return productMapper.toResponseList(products.stream()
                .filter(p -> p.getBusinessId().equals(businessId))
                .toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getRecentlyAddedProducts(Long businessId, int limit) {
        log.debug("Getting recently added {} products for business ID: {}", limit, businessId);

        List<Product> products = productRepository.findByBusinessIdAndIsActiveTrueAndIsAvailableTrue(businessId);
        return productMapper.toResponseList(products.stream()
                .limit(limit)
                .toList());
    }

    public boolean canFulfillOrder(Long productId, int quantity){
        log.debug("Checking if order can be fulfilled for product ID: {} quantity: {}", productId, quantity);

        return isProductInStock(productId, quantity);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }


    private void validateProductData(ProductCreateRequest request) {
        // Validate pricing
        if (request.getDiscountedPrice() != null &&
                request.getDiscountedPrice().compareTo(request.getBasePrice()) >= 0) {
            throw new InvalidProductDataException("Discounted price must be less than base price");
        }

        // Validate order quantities
        if (request.getMinOrderQuantity() > request.getMaxOrderQuantity()) {
            throw new InvalidProductDataException("Minimum order quantity cannot be greater than maximum order quantity");
        }

        // Validate stock levels
        if (request.getMinStockLevel() > request.getMaxStockLevel()) {
            throw new InvalidProductDataException("Minimum stock level cannot be greater than maximum stock level");
        }

        if (request.getReorderPoint() > request.getMaxStockLevel()) {
            throw new InvalidProductDataException("Reorder point cannot be greater than maximum stock level");
        }
    }

    private void validateProductUpdateData(ProductUpdateRequest request) {
        // Validate pricing
        if (request.getDiscountedPrice() != null &&
                request.getDiscountedPrice().compareTo(request.getBasePrice()) >= 0) {
            throw new InvalidProductDataException("Discounted price must be less than base price");
        }

        // Validate order quantities
        if (request.getMinOrderQuantity() > request.getMaxOrderQuantity()) {
            throw new InvalidProductDataException("Minimum order quantity cannot be greater than maximum order quantity");
        }
    }

    private ProductInventory createInventoryRecord(Product product, ProductCreateRequest request) {
        return ProductInventory.builder()
                .product(product)
                .currentStock(request.getInitialStock())
                .reservedStock(0)
                .minStockLevel(request.getMinStockLevel())
                .maxStockLevel(request.getMaxStockLevel())
                .reorderPoint(request.getReorderPoint())
                .reorderQuantity(request.getReorderQuantity())
                .warehouseLocation(request.getWarehouseLocation())
                .build();
    }
}
