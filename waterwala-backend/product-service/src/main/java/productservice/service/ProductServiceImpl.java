package productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productservice.dto.ProductCreateRequest;
import productservice.dto.ProductResponse;
import productservice.dto.ProductUpdateRequest;
import productservice.exceptions.*;
import productservice.mapper.ProductMapper;
import productservice.model.Product;
import productservice.model.ProductCategory;
import productservice.model.ProductType;
import productservice.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MongoTemplate mongoTemplate;
    private final ProductValidationService validationService;

    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {
        log.info("Creating product with SKU: {} for business: {}", request.getSku(), request.getBusinessId());

        // CRITICAL: Validate business before creating product
        validationService.validateProductCreationRequirements(request.getBusinessId());

        if (productRepository.findBySku(request.getSku()).isPresent()) {
            throw new DuplicateSkuException(request.getSku());
        }

        validateProductData(request);

        try {
            Product product = productMapper.toEntity(request);
            Product savedProduct = productRepository.save(product);

            log.info("Product created successfully with ID: {}", savedProduct.getId());
            return productMapper.toResponse(savedProduct);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            throw new ProductServiceException("Failed to create product", e);
        }
    }

    @Override
    public ProductResponse updateProduct(String id, ProductUpdateRequest request) {
        log.info("Updating product with ID: {}", id);

        Product product = findProductById(id);

        // CRITICAL: Validate business ownership before updating
        validationService.validateProductUpdateRequirements(product.getBusinessId());

        // Validate SKU uniqueness for update
        if (!isSkuUniqueForUpdate(request.getSku(), id)) {
            throw new DuplicateSkuException(request.getSku());
        }

        validateProductUpdateData(request);

        try {
            productMapper.updateEntity(product, request);
            Product updatedProduct = productRepository.save(product);

            log.info("Product updated successfully with ID: {}", id);
            return productMapper.toResponse(updatedProduct);

        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage(), e);
            throw new ProductServiceException("Failed to update product", e);
        }
    }

    @Override
    public void deleteProduct(String id) {
        log.info("Deleting product with ID: {}", id);
        Product product = findProductById(id);

        // CRITICAL: Validate business ownership before deleting
        validationService.validateProductUpdateRequirements(product.getBusinessId());

        try {
            productRepository.delete(product);
            log.info("Product deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting product: {}", e.getMessage(), e);
            throw new ProductServiceException("Failed to delete product", e);
        }
    }

    @Override
    public void toggleProductStatus(String id, boolean isActive) {
        log.info("Toggling product status for ID: {} to {}", id, isActive);

        try {
            Query query = new Query(Criteria.where("id").is(id));
            Update update = new Update()
                    .set("isActive", isActive)
                    .set("updatedAt", LocalDateTime.now());

            mongoTemplate.updateFirst(query, update, Product.class);
            log.info("Product status updated successfully for ID: {}", id);
        } catch (Exception e) {
            log.error("Error updating product status: {}", e.getMessage(), e);
            throw new ProductServiceException("Failed to update product status", e);
        }
    }

    @Override
    public void toggleProductAvailability(String id, boolean isAvailable) {
        log.info("Toggling product availability for ID: {} to {}", id, isAvailable);

        try {
            Query query = new Query(Criteria.where("id").is(id));
            Update update = new Update()
                    .set("isAvailable", isAvailable)
                    .set("updatedAt", LocalDateTime.now());

            mongoTemplate.updateFirst(query, update, Product.class);
            log.info("Product availability updated successfully for ID: {}", id);
        } catch (Exception e) {
            log.error("Error updating product availability: {}", e.getMessage(), e);
            throw new ProductServiceException("Failed to update product availability", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(String id) {
        log.debug("Getting product by ID: {}", id);
        Product product = findProductById(id);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        log.debug("Getting product by SKU: {}", sku);
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductByBusiness(String businessId) {
        log.debug("Getting products by businessId: {}", businessId);
        // Convert String to Long for repository call
        Long businessIdLong = Long.valueOf(businessId);
        List<Product> products = productRepository.findByBusinessId(businessIdLong);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProductByBusiness(String businessId) {
        log.debug("Getting active products by business ID: {}", businessId);
        Long businessIdLong = Long.valueOf(businessId);
        List<Product> products = productRepository.findByBusinessIdAndIsActiveTrueAndIsAvailableTrue(businessIdLong);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAvailableProductByBusiness(String businessId) {
        log.debug("Getting available products by business ID: {}", businessId);
        Long businessIdLong = Long.valueOf(businessId);
        List<Product> products = productRepository.findByBusinessIdAndIsActiveTrueAndIsAvailableTrue(businessIdLong);
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
        String regex = ".*" + name + ".*";
        List<Product> products = productRepository.findByNameRegexAndIsActiveTrueAndIsAvailableTrue(regex);
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
    public List<ProductResponse> getProductByCategoryAndBusiness(ProductCategory category, String businessId) {
        log.debug("Getting products by category: {} and business ID: {}", category, businessId);
        Long businessIdLong = Long.valueOf(businessId);
        List<Product> products = productRepository.findByCategoryAndBusinessIdAndIsActiveTrueAndIsAvailableTrue(category, businessIdLong);
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
    public Page<ProductResponse> getProductsByBusiness(String businessId, Pageable pageable) {
        log.debug("Getting products by business ID: {} with pagination", businessId);
        Long businessIdLong = Long.valueOf(businessId);
        Page<Product> products = productRepository.findByBusinessIdAndIsActiveTrueAndIsAvailableTrue(businessIdLong, pageable);
        return products.map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String searchTerm, Pageable pageable) {
        log.debug("Searching products with term: {}", searchTerm);
        List<Product> products = productRepository.searchAvailableActiveProducts(searchTerm);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), products.size());
        List<Product> pageContent = products.subList(start, end);

        return new org.springframework.data.domain.PageImpl<>(
                productMapper.toResponseList(pageContent),
                pageable,
                products.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long countProductsByBusiness(String businessId) {
        log.debug("Counting products by business ID: {}", businessId);
        Long businessIdLong = Long.valueOf(businessId);
        return productRepository.countByBusinessIdAndIsActiveTrue(businessIdLong);
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
    public void updateProductStock(String productId, int quantity) {
        log.info("Updating stock for product ID: {} by quantity: {}", productId, quantity);

        try {
            Query query = new Query(Criteria.where("id").is(productId));
            Update update = new Update()
                    .inc("inventory.currentStock", quantity)
                    .set("inventory.lastUpdated", LocalDateTime.now())
                    .set("updatedAt", LocalDateTime.now());

            mongoTemplate.updateFirst(query, update, Product.class);
            log.info("Stock updated successfully for product ID: {}", productId);

        } catch (Exception e) {
            log.error("Error updating stock: {}", e.getMessage(), e);
            throw new ProductServiceException("Failed to update stock", e);
        }
    }

    @Override
    public void reserveProductStock(String productId, int quantity) {
        log.info("Reserving stock for productId {} quantity {}", productId, quantity);

        if (!canFulfillOrder(productId, quantity)) {
            Product product = findProductById(productId);
            int currentStock = product.getInventory() != null ? product.getInventory().getCurrentStock() : 0;
            throw new InsufficientStockException(productId, quantity, currentStock);
        }

        try {
            Query query = new Query(Criteria.where("id").is(productId));
            Update update = new Update()
                    .inc("inventory.currentStock", -quantity)
                    .inc("inventory.reservedStock", quantity)
                    .set("inventory.lastUpdated", LocalDateTime.now())
                    .set("updatedAt", LocalDateTime.now());

            mongoTemplate.updateFirst(query, update, Product.class);
            log.info("Stock reserved successfully for productId: {}", productId);

        } catch (Exception e) {
            log.error("Error reserving stock: {}", e.getMessage(), e);
            throw new ProductServiceException("Failed to reserve stock", e);
        }
    }

    @Override
    public void releaseProductStock(String productId, int quantity) {
        log.info("Releasing product stock for productId: {} quantity: {}", productId, quantity);

        try {
            Query query = new Query(Criteria.where("id").is(productId));
            Update update = new Update()
                    .inc("inventory.reservedStock", -quantity)
                    .inc("inventory.currentStock", quantity)
                    .set("inventory.lastUpdated", LocalDateTime.now())
                    .set("updatedAt", LocalDateTime.now());

            mongoTemplate.updateFirst(query, update, Product.class);
            log.info("Stock released successfully for productId: {}", productId);

        } catch (Exception e) {
            log.error("Error releasing stock: {}", e.getMessage(), e);
            throw new ProductServiceException("Failed to release stock", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInStock(String productId, int requiredQuantity) {
        log.debug("Checking if product ID: {} has sufficient stock for quantity: {}", productId, requiredQuantity);
        return productRepository.findByIdWithSufficientStock(productId, requiredQuantity).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSkuUnique(String sku) {
        log.debug("Checking if SKU is unique: {}", sku);
        return productRepository.findBySku(sku).isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSkuUniqueForUpdate(String sku, String productId) {
        log.debug("Checking if SKU is unique for update: {} excluding product ID: {}", sku, productId);
        return !productRepository.existsBySkuAndIdNot(sku, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canFulfillOrder(String productId, int quantity) {
        log.debug("Checking if order can be fulfilled for product ID: {} quantity: {}", productId, quantity);
        return isProductInStock(productId, quantity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getTopSellingProducts(String businessId, int limit) {
        log.debug("Getting top {} selling products for business ID: {}", limit, businessId);
        Long businessIdLong = Long.valueOf(businessId);
        Pageable pageable = PageRequest.of(0, limit);
        List<Product> products = productRepository.findTopSellingProductsByBusiness(businessIdLong, pageable);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getRecentlyAddedProducts(String businessId, int limit) {
        log.debug("Getting recently added {} products for business ID: {}", limit, businessId);
        Long businessIdLong = Long.valueOf(businessId);
        List<Product> products = productRepository.findByBusinessIdAndIsActiveTrueAndIsAvailableTrue(businessIdLong);
        return productMapper.toResponseList(products.stream()
                .limit(limit)
                .toList());
    }

    // Helper methods
    private Product findProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
    }

    private void validateProductData(ProductCreateRequest request) {
        if (request.getDiscountedPrice() != null &&
                request.getDiscountedPrice().compareTo(request.getBasePrice()) >= 0) {
            throw new InvalidProductDataException("Discounted price must be less than base price");
        }

        if (request.getMinOrderQuantity() > request.getMaxOrderQuantity()) {
            throw new InvalidProductDataException("Minimum order quantity cannot be greater than maximum order quantity");
        }

        if (request.getMinStockLevel() > request.getMaxStockLevel()) {
            throw new InvalidProductDataException("Minimum stock level cannot be greater than maximum stock level");
        }

        if (request.getReorderPoint() > request.getMaxStockLevel()) {
            throw new InvalidProductDataException("Reorder point cannot be greater than maximum stock level");
        }
    }

    private void validateProductUpdateData(ProductUpdateRequest request) {
        if (request.getDiscountedPrice() != null &&
                request.getDiscountedPrice().compareTo(request.getBasePrice()) >= 0) {
            throw new InvalidProductDataException("Discounted price must be less than base price");
        }

        if (request.getMinOrderQuantity() > request.getMaxOrderQuantity()) {
            throw new InvalidProductDataException("Minimum order quantity cannot be greater than maximum order quantity");
        }
    }
}