// src/services/productService.js
import { apiClient, API_BASE_URLS } from './api.js';

const PRODUCT_BASE_URL = API_BASE_URLS.PRODUCT_SERVICE;

/**
 * Product Service - Handles all product-related API calls
 */
export const productService = {
  // Product Categories enum
  PRODUCT_CATEGORIES: {
    WATER_CANS: 'WATER_CANS',
    WATER_CONTAINERS: 'WATER_CONTAINERS',
    BULK_WATER: 'BULK_WATER',
    WATER_TANKS: 'WATER_TANKS',
    INSTALLATION_SERVICES: 'INSTALLATION_SERVICES',
    MAINTENANCE_SERVICES: 'MAINTENANCE_SERVICES',
    EQUIPMENT_RENTAL: 'EQUIPMENT_RENTAL',
    WATER_TESTING: 'WATER_TESTING',
    PURIFIERS: 'PURIFIERS',
    ACCESSORIES: 'ACCESSORIES'
  },

  // Product Types enum
  PRODUCT_TYPES: {
    PRODUCT: 'PRODUCT',
    SERVICE: 'SERVICE',
    SUBSCRIPTION: 'SUBSCRIPTION'
  },

  // Sort Fields for products
  SORT_FIELDS: {
    CREATED_AT: 'createdAt',
    UPDATED_AT: 'updatedAt',
    NAME: 'name',
    PRICE: 'basePrice',
    DISCOUNTED_PRICE: 'discountedPrice',
    STOCK: 'availableQuantity',
    CATEGORY: 'category'
  },

  // Sort Directions
  SORT_DIRECTIONS: {
    ASC: 'asc',
    DESC: 'desc'
  },

  // ===============================
  // PRODUCT CRUD OPERATIONS
  // ===============================

  /**
   * Create a new product
   * @param {Object} productData - Product creation data
   * @param {string} productData.name - Product name (2-255 chars)
   * @param {string} productData.description - Product description (max 1000 chars)
   * @param {string} productData.sku - Stock Keeping Unit (3-50 chars)
   * @param {string} productData.category - Product category
   * @param {string} productData.type - Product type (PRODUCT, SERVICE, SUBSCRIPTION)
   * @param {number} productData.basePrice - Base price (min 0.01)
   * @param {number} productData.discountedPrice - Discounted price (optional)
   * @param {number} productData.availableQuantity - Available quantity
   * @param {number} productData.minOrderQuantity - Minimum order quantity
   * @param {number} productData.maxOrderQuantity - Maximum order quantity
   * @param {string} productData.unit - Unit of measurement
   * @param {string} productData.businessId - Business identifier
   * @param {string} productData.brand - Brand name (optional)
   * @param {string} productData.imageUrl - Primary image URL (optional)
   * @param {Array} productData.additionalImages - Additional image URLs (optional)
   * @param {Array} productData.specifications - Product specifications (optional)
   * @param {Array} productData.pricingTiers - Pricing tiers (optional)
   * @param {number} productData.initialStock - Initial stock quantity
   * @param {number} productData.minStockLevel - Minimum stock level
   * @param {number} productData.maxStockLevel - Maximum stock level
   * @param {number} productData.reorderPoint - Reorder point
   * @param {number} productData.reorderQuantity - Reorder quantity
   * @param {string} productData.warehouseLocation - Warehouse location (optional)
   * @returns {Promise<Object>} Created product data
   */
  createProduct: async (productData) => {
    try {
      const validatedData = productService.validateProductData(productData);

      const response = await apiClient.post(`${PRODUCT_BASE_URL}/products`, validatedData);
      return response.data;
    } catch (error) {
      if (error.response?.status === 400) {
        throw new Error(error.response?.data?.message || 'Invalid product data');
      }
      if (error.response?.status === 409) {
        throw new Error('Product with this SKU already exists');
      }
      throw new Error(error.response?.data?.message || 'Failed to create product');
    }
  },

  /**
   * Get product by ID
   * @param {string} productId - Product ID
   * @returns {Promise<Object>} Product data with inventory
   */
  getProductById: async (productId) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/${productId}`);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Product not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to fetch product');
    }
  },

  /**
   * Get product by SKU
   * @param {string} sku - Product SKU
   * @returns {Promise<Object>} Product data
   */
  getProductBySku: async (sku) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/sku/${sku}`);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Product not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to fetch product by SKU');
    }
  },

  /**
   * Update product
   * @param {string} productId - Product ID
   * @param {Object} updateData - Product update data
   * @returns {Promise<Object>} Updated product data
   */
  updateProduct: async (productId, updateData) => {
    try {
      const validatedData = productService.validateProductUpdateData(updateData);

      const response = await apiClient.put(`${PRODUCT_BASE_URL}/products/${productId}`, validatedData);
      return response.data;
    } catch (error) {
      if (error.response?.status === 400) {
        throw new Error(error.response?.data?.message || 'Invalid update data');
      }
      if (error.response?.status === 404) {
        throw new Error('Product not found');
      }
      if (error.response?.status === 409) {
        throw new Error('SKU already exists for another product');
      }
      throw new Error(error.response?.data?.message || 'Failed to update product');
    }
  },

  /**
   * Delete product
   * @param {string} productId - Product ID
   * @returns {Promise<void>} Deletion confirmation
   */
  deleteProduct: async (productId) => {
    try {
      await apiClient.delete(`${PRODUCT_BASE_URL}/products/${productId}`);
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Product not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to delete product');
    }
  },

  // ===============================
  // PRODUCT LISTING & SEARCH
  // ===============================

  /**
   * Get all available products with pagination
   * @param {Object} pageParams - Pagination parameters
   * @param {number} pageParams.page - Page number (0-based, default: 0)
   * @param {number} pageParams.size - Page size (default: 20)
   * @param {string} pageParams.sortBy - Sort field (default: 'createdAt')
   * @param {string} pageParams.sortDirection - Sort direction (default: 'desc')
   * @returns {Promise<Object>} Paginated product data
   */
  getAllAvailableProducts: async (pageParams = {}) => {
    try {
      const {
        page = 0,
        size = 20,
        sortBy = 'createdAt',
        sortDirection = 'desc'
      } = pageParams;

      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products`, {
        params: { page, size, sortBy, sortDirection }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch products');
    }
  },

  /**
   * Search products by name
   * @param {string} searchTerm - Search term
   * @returns {Promise<Array>} List of matching products
   */
  searchProductByName: async (searchTerm) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/search`, {
        params: { name: searchTerm }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to search products');
    }
  },

  /**
   * Search products with pagination
   * @param {string} searchTerm - Search term
   * @param {Object} pageParams - Pagination parameters
   * @returns {Promise<Object>} Paginated search results
   */
  searchProductsPaginated: async (searchTerm, pageParams = {}) => {
    try {
      const {
        page = 0,
        size = 20,
        sortBy = 'createdAt',
        sortDirection = 'desc'
      } = pageParams;

      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/search/page`, {
        params: { searchTerm, page, size, sortBy, sortDirection }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to search products');
    }
  },

  // ===============================
  // CATEGORY & FILTERING
  // ===============================

  /**
   * Get products by category
   * @param {string} category - Product category
   * @returns {Promise<Array>} List of products in category
   */
  getProductsByCategory: async (category) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/category/${category}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch products by category');
    }
  },

  /**
   * Get products by category with pagination
   * @param {string} category - Product category
   * @param {Object} pageParams - Pagination parameters
   * @returns {Promise<Object>} Paginated products by category
   */
  getProductsByCategoryPaginated: async (category, pageParams = {}) => {
    try {
      const {
        page = 0,
        size = 20,
        sortBy = 'createdAt',
        sortDirection = 'desc'
      } = pageParams;

      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/category/${category}/page`, {
        params: { page, size, sortBy, sortDirection }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch products by category');
    }
  },

  /**
   * Get products by multiple categories
   * @param {Array} categories - Array of categories
   * @returns {Promise<Array>} List of products matching any category
   */
  getProductsByMultipleCategories: async (categories) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/categories`, {
        params: { categories }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch products by categories');
    }
  },

  /**
   * Get products by category and business
   * @param {string} category - Product category
   * @param {string} businessId - Business ID
   * @returns {Promise<Array>} List of products
   */
  getProductsByCategoryAndBusiness: async (category, businessId) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/category/${category}/business/${businessId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch products by category and business');
    }
  },

  /**
   * Get products by type
   * @param {string} type - Product type (PRODUCT, SERVICE, SUBSCRIPTION)
   * @returns {Promise<Array>} List of products by type
   */
  getProductsByType: async (type) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/type/${type}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch products by type');
    }
  },

  /**
   * Get products by brand
   * @param {string} brand - Brand name
   * @returns {Promise<Array>} List of products by brand
   */
  getProductsByBrand: async (brand) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/brand/${brand}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch products by brand');
    }
  },

  /**
   * Get products in price range
   * @param {number} minPrice - Minimum price
   * @param {number} maxPrice - Maximum price
   * @returns {Promise<Array>} List of products in price range
   */
  getProductsInPriceRange: async (minPrice, maxPrice) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/price-range`, {
        params: { minPrice, maxPrice }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch products by price range');
    }
  },

  // ===============================
  // BUSINESS-SPECIFIC PRODUCTS
  // ===============================

  /**
   * Get all products by business
   * @param {string} businessId - Business ID
   * @returns {Promise<Array>} List of all business products
   */
  getProductsByBusiness: async (businessId) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/business/${businessId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch products by business');
    }
  },

  /**
   * Get products by business with pagination
   * @param {string} businessId - Business ID
   * @param {Object} pageParams - Pagination parameters
   * @returns {Promise<Object>} Paginated business products
   */
  getProductsByBusinessPaginated: async (businessId, pageParams = {}) => {
    try {
      const {
        page = 0,
        size = 20,
        sortBy = 'createdAt',
        sortDirection = 'desc'
      } = pageParams;

      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/business/${businessId}/page`, {
        params: { page, size, sortBy, sortDirection }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch business products');
    }
  },

  /**
   * Get active products by business
   * @param {string} businessId - Business ID
   * @returns {Promise<Array>} List of active business products
   */
  getActiveProductsByBusiness: async (businessId) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/business/${businessId}/active`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch active business products');
    }
  },

  /**
   * Get available products by business
   * @param {string} businessId - Business ID
   * @returns {Promise<Array>} List of available business products
   */
  getAvailableProductsByBusiness: async (businessId) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/business/${businessId}/available`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch available business products');
    }
  },

  /**
   * Count products by business
   * @param {string} businessId - Business ID
   * @returns {Promise<number>} Count of active products
   */
  countProductsByBusiness: async (businessId) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/business/${businessId}/count`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to count business products');
    }
  },

  /**
   * Get top selling products for business
   * @param {string} businessId - Business ID
   * @param {number} limit - Maximum number of products (default: 10)
   * @returns {Promise<Array>} List of top selling products
   */
  getTopSellingProducts: async (businessId, limit = 10) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/business/${businessId}/top-selling`, {
        params: { limit }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch top selling products');
    }
  },

  /**
   * Get recently added products for business
   * @param {string} businessId - Business ID
   * @param {number} limit - Maximum number of products (default: 10)
   * @returns {Promise<Array>} List of recently added products
   */
  getRecentlyAddedProducts: async (businessId, limit = 10) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/business/${businessId}/recent`, {
        params: { limit }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch recently added products');
    }
  },

  // ===============================
  // INVENTORY MANAGEMENT
  // ===============================

  /**
   * Update product stock
   * @param {string} productId - Product ID
   * @param {number} quantity - Quantity to add/subtract from current stock
   * @returns {Promise<Object>} Update response
   */
  updateProductStock: async (productId, quantity) => {
    try {
      const response = await apiClient.patch(`${PRODUCT_BASE_URL}/products/${productId}/stock`, null, {
        params: { quantity }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Product not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to update product stock');
    }
  },

  /**
   * Reserve product stock
   * @param {string} productId - Product ID
   * @param {number} quantity - Quantity to reserve
   * @returns {Promise<Object>} Reservation response
   */
  reserveProductStock: async (productId, quantity) => {
    try {
      const response = await apiClient.post(`${PRODUCT_BASE_URL}/products/${productId}/reserve-stock`, null, {
        params: { quantity }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 400) {
        throw new Error('Insufficient stock available');
      }
      if (error.response?.status === 404) {
        throw new Error('Product not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to reserve stock');
    }
  },

  /**
   * Release reserved stock
   * @param {string} productId - Product ID
   * @param {number} quantity - Quantity to release from reserved stock
   * @returns {Promise<Object>} Release response
   */
  releaseProductStock: async (productId, quantity) => {
    try {
      const response = await apiClient.post(`${PRODUCT_BASE_URL}/products/${productId}/release-stock`, null, {
        params: { quantity }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Product not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to release stock');
    }
  },

  /**
   * Check if product is in stock
   * @param {string} productId - Product ID
   * @param {number} requiredQuantity - Required quantity
   * @returns {Promise<boolean>} Whether product is in stock
   */
  isProductInStock: async (productId, requiredQuantity) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/${productId}/stock-check`, {
        params: { requiredQuantity }
      });
      return response.data;
    } catch (error) {
      return false;
    }
  },

  /**
   * Check if order can be fulfilled
   * @param {string} productId - Product ID
   * @param {number} quantity - Order quantity
   * @returns {Promise<boolean>} Whether order can be fulfilled
   */
  canFulfillOrder: async (productId, quantity) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/${productId}/can-fulfill/${quantity}`);
      return response.data;
    } catch (error) {
      return false;
    }
  },

  /**
   * Get products with low stock
   * @returns {Promise<Array>} List of products with low stock
   */
  getProductsWithLowStock: async () => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/low-stock`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch low stock products');
    }
  },

  // ===============================
  // PRODUCT STATUS MANAGEMENT
  // ===============================

  /**
   * Toggle product active status
   * @param {string} productId - Product ID
   * @param {boolean} isActive - Active status
   * @returns {Promise<Object>} Update response
   */
  toggleProductStatus: async (productId, isActive) => {
    try {
      const response = await apiClient.patch(`${PRODUCT_BASE_URL}/products/${productId}/status`, null, {
        params: { isActive }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Product not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to update product status');
    }
  },

  /**
   * Toggle product availability
   * @param {string} productId - Product ID
   * @param {boolean} isAvailable - Availability status
   * @returns {Promise<Object>} Update response
   */
  toggleProductAvailability: async (productId, isAvailable) => {
    try {
      const response = await apiClient.patch(`${PRODUCT_BASE_URL}/products/${productId}/availability`, null, {
        params: { isAvailable }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Product not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to update product availability');
    }
  },

  // ===============================
  // SKU VALIDATION
  // ===============================

  /**
   * Check if SKU is unique
   * @param {string} sku - SKU to check
   * @returns {Promise<boolean>} Whether SKU is unique
   */
  isSkuUnique: async (sku) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/validate/sku/${sku}`);
      return response.data;
    } catch (error) {
      return false;
    }
  },

  /**
   * Check if SKU is unique for update (excluding current product)
   * @param {string} sku - SKU to check
   * @param {string} productId - Product ID to exclude
   * @returns {Promise<boolean>} Whether SKU is unique
   */
  isSkuUniqueForUpdate: async (sku, productId) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/validate/sku/${sku}/exclude/${productId}`);
      return response.data;
    } catch (error) {
      return false;
    }
  },

  // ===============================
  // CONVENIENCE METHODS
  // ===============================

  /**
   * Get latest products
   * @param {number} limit - Maximum number of products (default: 10)
   * @returns {Promise<Array>} List of latest products
   */
  getLatestProducts: async (limit = 10) => {
    try {
      const response = await apiClient.get(`${PRODUCT_BASE_URL}/products/latest`, {
        params: { limit }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch latest products');
    }
  },

  // ===============================
  // UTILITY METHODS
  // ===============================

  /**
   * Validate product creation data
   * @param {Object} productData - Product data to validate
   * @returns {Object} Validated product data
   */
  validateProductData: (productData) => {
    const errors = [];
    const validated = { ...productData };

    // Required fields validation
    if (!validated.name || validated.name.trim().length < 2 || validated.name.length > 255) {
      errors.push('Product name must be between 2 and 255 characters');
    }

    if (!validated.sku || validated.sku.trim().length < 3 || validated.sku.length > 50) {
      errors.push('SKU must be between 3 and 50 characters');
    }

    if (!validated.category || !Object.values(productService.PRODUCT_CATEGORIES).includes(validated.category)) {
      errors.push('Valid product category is required');
    }

    if (!validated.type || !Object.values(productService.PRODUCT_TYPES).includes(validated.type)) {
      errors.push('Valid product type is required (PRODUCT, SERVICE, SUBSCRIPTION)');
    }

    if (!validated.basePrice || validated.basePrice < 0.01) {
      errors.push('Base price must be at least 0.01');
    }

    if (validated.discountedPrice && validated.discountedPrice < 0.01) {
      errors.push('Discounted price must be at least 0.01');
    }

    if (validated.availableQuantity === undefined || validated.availableQuantity < 0) {
      errors.push('Available quantity must be 0 or greater');
    }

    if (!validated.minOrderQuantity || validated.minOrderQuantity < 1) {
      errors.push('Minimum order quantity must be at least 1');
    }

    if (!validated.maxOrderQuantity || validated.maxOrderQuantity < 1) {
      errors.push('Maximum order quantity must be at least 1');
    }

    if (validated.maxOrderQuantity < validated.minOrderQuantity) {
      errors.push('Maximum order quantity must be greater than or equal to minimum order quantity');
    }

    if (!validated.unit || validated.unit.trim().length === 0) {
      errors.push('Unit of measurement is required');
    }

    if (!validated.businessId || validated.businessId.trim().length === 0) {
      errors.push('Business ID is required');
    }

    // Inventory validation
    if (validated.initialStock === undefined || validated.initialStock < 0) {
      errors.push('Initial stock must be 0 or greater');
    }

    if (validated.minStockLevel === undefined || validated.minStockLevel < 0) {
      errors.push('Minimum stock level must be 0 or greater');
    }

    if (!validated.maxStockLevel || validated.maxStockLevel < 1) {
      errors.push('Maximum stock level must be at least 1');
    }

    if (validated.reorderPoint === undefined || validated.reorderPoint < 0) {
      errors.push('Reorder point must be 0 or greater');
    }

    if (!validated.reorderQuantity || validated.reorderQuantity < 1) {
      errors.push('Reorder quantity must be at least 1');
    }

    // Optional fields validation
    if (validated.description && validated.description.length > 1000) {
      errors.push('Description must be less than 1000 characters');
    }

    if (errors.length > 0) {
      throw new Error(errors.join(', '));
    }

    return validated;
  },

  /**
   * Validate product update data
   * @param {Object} updateData - Update data to validate
   * @returns {Object} Validated update data
   */
  validateProductUpdateData: (updateData) => {
    const errors = [];
    const validated = { ...updateData };

    // Name validation
    if (validated.name !== undefined) {
      if (!validated.name || validated.name.trim().length < 2 || validated.name.length > 255) {
        errors.push('Product name must be between 2 and 255 characters');
      }
    }

    // SKU validation
    if (validated.sku !== undefined) {
      if (!validated.sku || validated.sku.trim().length < 3 || validated.sku.length > 50) {
        errors.push('SKU must be between 3 and 50 characters');
      }
    }

    // Category validation
    if (validated.category !== undefined) {
      if (!Object.values(productService.PRODUCT_CATEGORIES).includes(validated.category)) {
        errors.push('Valid product category is required');
      }
    }

    // Type validation
    if (validated.type !== undefined) {
      if (!Object.values(productService.PRODUCT_TYPES).includes(validated.type)) {
        errors.push('Valid product type is required');
      }
    }

    // Price validations
    if (validated.basePrice !== undefined && validated.basePrice < 0.01) {
      errors.push('Base price must be at least 0.01');
    }

    if (validated.discountedPrice !== undefined && validated.discountedPrice < 0.01) {
      errors.push('Discounted price must be at least 0.01');
    }

    // Quantity validations
    if (validated.availableQuantity !== undefined && validated.availableQuantity < 0) {
      errors.push('Available quantity must be 0 or greater');
    }

    if (validated.minOrderQuantity !== undefined && validated.minOrderQuantity < 1) {
      errors.push('Minimum order quantity must be at least 1');
    }

    if (validated.maxOrderQuantity !== undefined && validated.maxOrderQuantity < 1) {
      errors.push('Maximum order quantity must be at least 1');
    }

    // Check order quantity relationship
    if (validated.minOrderQuantity !== undefined && validated.maxOrderQuantity !== undefined) {
      if (validated.maxOrderQuantity < validated.minOrderQuantity) {
        errors.push('Maximum order quantity must be greater than or equal to minimum order quantity');
      }
    }

    // Unit validation
    if (validated.unit !== undefined) {
      if (!validated.unit || validated.unit.trim().length === 0) {
        errors.push('Unit of measurement is required');
      }
    }

    // Description validation
    if (validated.description !== undefined && validated.description.length > 1000) {
      errors.push('Description must be less than 1000 characters');
    }

    if (errors.length > 0) {
      throw new Error(errors.join(', '));
    }

    return validated;
  },

  /**
   * Get category display name
   * @param {string} category - Category key
   * @returns {string} Display name
   */
  getCategoryDisplayName: (category) => {
    const categoryNames = {
      [productService.PRODUCT_CATEGORIES.WATER_CANS]: 'Water Cans',
      [productService.PRODUCT_CATEGORIES.WATER_CONTAINERS]: 'Water Containers',
      [productService.PRODUCT_CATEGORIES.BULK_WATER]: 'Bulk Water',
      [productService.PRODUCT_CATEGORIES.WATER_TANKS]: 'Water Tanks',
      [productService.PRODUCT_CATEGORIES.INSTALLATION_SERVICES]: 'Installation Services',
      [productService.PRODUCT_CATEGORIES.MAINTENANCE_SERVICES]: 'Maintenance Services',
      [productService.PRODUCT_CATEGORIES.EQUIPMENT_RENTAL]: 'Equipment Rental',
      [productService.PRODUCT_CATEGORIES.WATER_TESTING]: 'Water Testing',
      [productService.PRODUCT_CATEGORIES.PURIFIERS]: 'Purifiers',
      [productService.PRODUCT_CATEGORIES.ACCESSORIES]: 'Accessories'
    };

    return categoryNames[category] || category;
  },

  /**
   * Get type display name
   * @param {string} type - Product type
   * @returns {string} Display name
   */
  getTypeDisplayName: (type) => {
    const typeNames = {
      [productService.PRODUCT_TYPES.PRODUCT]: 'Physical Product',
      [productService.PRODUCT_TYPES.SERVICE]: 'Service',
      [productService.PRODUCT_TYPES.SUBSCRIPTION]: 'Subscription'
    };

    return typeNames[type] || type;
  },

  /**
   * Format price for display
   * @param {number} price - Price value
   * @param {string} currency - Currency symbol (default: '₹')
   * @returns {string} Formatted price
   */
  formatPrice: (price, currency = '₹') => {
    if (!price || price === 0) return `${currency}0`;
    return `${currency}${price.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  },

  /**
   * Calculate discount percentage
   * @param {number} basePrice - Original price
   * @param {number} discountedPrice - Discounted price
   * @returns {number} Discount percentage
   */
  calculateDiscountPercentage: (basePrice, discountedPrice) => {
    if (!basePrice || !discountedPrice || discountedPrice >= basePrice) return 0;
    return Math.round(((basePrice - discountedPrice) / basePrice) * 100);
  },

  /**
   * Get stock status based on inventory levels
   * @param {Object} inventory - Inventory object
   * @returns {Object} Stock status info
   */
  getStockStatus: (inventory) => {
    if (!inventory) {
      return { status: 'unknown', color: 'gray', message: 'Stock unknown' };
    }

    const { currentStock, minStockLevel, reorderPoint } = inventory;

    if (currentStock === 0) {
      return { status: 'out-of-stock', color: 'red', message: 'Out of stock' };
    }

    if (currentStock <= minStockLevel) {
      return { status: 'critical', color: 'red', message: 'Critical stock level' };
    }

    if (currentStock <= reorderPoint) {
      return { status: 'low', color: 'yellow', message: 'Low stock - reorder needed' };
    }

    return { status: 'good', color: 'green', message: 'In stock' };
  },

  /**
   * Get product availability status
   * @param {Object} product - Product object
   * @returns {Object} Availability status
   */
  getAvailabilityStatus: (product) => {
    if (!product) {
      return { available: false, reason: 'Product not found' };
    }

    if (!product.isActive) {
      return { available: false, reason: 'Product is inactive' };
    }

    if (!product.isAvailable) {
      return { available: false, reason: 'Product is temporarily unavailable' };
    }

    if (product.availableQuantity === 0) {
      return { available: false, reason: 'Out of stock' };
    }

    return { available: true, reason: 'Available' };
  },

  /**
   * Calculate effective price based on quantity and pricing tiers
   * @param {Object} product - Product object with pricing tiers
   * @param {number} quantity - Order quantity
   * @returns {Object} Price calculation result
   */
  calculateEffectivePrice: (product, quantity) => {
    if (!product || !quantity) {
      return { pricePerUnit: 0, totalPrice: 0, tier: null, discount: 0 };
    }

    let effectivePrice = product.basePrice;
    let applicableTier = null;

    // Check for applicable pricing tiers
    if (product.pricingTiers && Array.isArray(product.pricingTiers)) {
      const applicableTiers = product.pricingTiers
        .filter(tier =>
          tier.isActive &&
          quantity >= tier.minQuantity &&
          quantity <= tier.maxQuantity
        )
        .sort((a, b) => a.pricePerUnit - b.pricePerUnit); // Sort by price (cheapest first)

      if (applicableTiers.length > 0) {
        applicableTier = applicableTiers[0];
        effectivePrice = applicableTier.pricePerUnit;
      }
    }

    // Use discounted price if no tier applies and discounted price exists
    if (!applicableTier && product.discountedPrice) {
      effectivePrice = product.discountedPrice;
    }

    const totalPrice = effectivePrice * quantity;
    const discount = product.basePrice - effectivePrice;

    return {
      pricePerUnit: effectivePrice,
      totalPrice,
      tier: applicableTier,
      discount: discount * quantity,
      discountPercentage: productService.calculateDiscountPercentage(product.basePrice, effectivePrice)
    };
  },

  /**
   * Filter products based on criteria
   * @param {Array} products - Array of products
   * @param {Object} filters - Filter criteria
   * @returns {Array} Filtered products
   */
  filterProducts: (products, filters = {}) => {
    if (!Array.isArray(products)) return [];

    return products.filter(product => {
      // Category filter
      if (filters.categories && filters.categories.length > 0) {
        if (!filters.categories.includes(product.category)) return false;
      }

      // Type filter
      if (filters.types && filters.types.length > 0) {
        if (!filters.types.includes(product.type)) return false;
      }

      // Price range filter
      if (filters.minPrice !== undefined && product.basePrice < filters.minPrice) {
        return false;
      }
      if (filters.maxPrice !== undefined && product.basePrice > filters.maxPrice) {
        return false;
      }

      // Brand filter
      if (filters.brands && filters.brands.length > 0) {
        if (!filters.brands.includes(product.brand)) return false;
      }

      // Availability filter
      if (filters.availableOnly) {
        const availability = productService.getAvailabilityStatus(product);
        if (!availability.available) return false;
      }

      // Active filter
      if (filters.activeOnly && !product.isActive) {
        return false;
      }

      // Stock filter
      if (filters.inStockOnly && product.availableQuantity === 0) {
        return false;
      }

      // Business filter
      if (filters.businessId && product.businessId !== filters.businessId) {
        return false;
      }

      return true;
    });
  },

  /**
   * Sort products by criteria
   * @param {Array} products - Array of products
   * @param {string} sortBy - Sort field
   * @param {string} sortOrder - Sort order (asc/desc)
   * @returns {Array} Sorted products
   */
  sortProducts: (products, sortBy = 'createdAt', sortOrder = 'desc') => {
    if (!Array.isArray(products)) return [];

    return [...products].sort((a, b) => {
      let valueA = a[sortBy];
      let valueB = b[sortBy];

      // Handle special cases
      if (sortBy === 'effectivePrice') {
        valueA = a.discountedPrice || a.basePrice;
        valueB = b.discountedPrice || b.basePrice;
      }

      // Handle different data types
      if (typeof valueA === 'string') {
        valueA = valueA.toLowerCase();
        valueB = valueB.toLowerCase();
      }

      if (valueA === valueB) return 0;

      const comparison = valueA < valueB ? -1 : 1;
      return sortOrder === 'desc' ? -comparison : comparison;
    });
  },

  /**
   * Create new product specification
   * @param {string} specKey - Specification key
   * @param {string} specValue - Specification value
   * @param {string} unit - Unit (optional)
   * @returns {Object} New specification object
   */
  createSpecification: (specKey, specValue, unit = '') => {
    return {
      specKey: specKey.trim(),
      specValue: specValue.trim(),
      unit: unit.trim()
    };
  },

  /**
   * Create new pricing tier
   * @param {number} minQuantity - Minimum quantity
   * @param {number} maxQuantity - Maximum quantity
   * @param {number} pricePerUnit - Price per unit
   * @param {number} discountPercentage - Discount percentage
   * @returns {Object} New pricing tier object
   */
  createPricingTier: (minQuantity, maxQuantity, pricePerUnit, discountPercentage = 0) => {
    return {
      minQuantity,
      maxQuantity,
      pricePerUnit,
      discountPercentage
    };
  },

  /**
   * Create new product template with default values
   * @param {string} businessId - Business ID
   * @param {Object} overrides - Values to override defaults
   * @returns {Object} New product template
   */
  createProductTemplate: (businessId, overrides = {}) => {
    return {
      name: '',
      description: '',
      sku: '',
      category: productService.PRODUCT_CATEGORIES.WATER_CANS,
      type: productService.PRODUCT_TYPES.PRODUCT,
      basePrice: 0,
      discountedPrice: null,
      availableQuantity: 0,
      minOrderQuantity: 1,
      maxOrderQuantity: 10,
      unit: 'pieces',
      businessId,
      brand: '',
      imageUrl: '',
      additionalImages: [],
      specifications: [],
      pricingTiers: [],
      initialStock: 0,
      minStockLevel: 10,
      maxStockLevel: 1000,
      reorderPoint: 20,
      reorderQuantity: 100,
      warehouseLocation: '',
      ...overrides
    };
  },

  /**
   * Extract unique values from products for filtering
   * @param {Array} products - Array of products
   * @param {string} field - Field to extract unique values from
   * @returns {Array} Array of unique values
   */
  getUniqueValues: (products, field) => {
    if (!Array.isArray(products)) return [];

    const values = products
      .map(product => product[field])
      .filter(value => value !== null && value !== undefined && value !== '');

    return [...new Set(values)].sort();
  },

  /**
   * Get product statistics
   * @param {Array} products - Array of products
   * @returns {Object} Product statistics
   */
  getProductStatistics: (products) => {
    if (!Array.isArray(products)) return {};

    const stats = {
      totalProducts: products.length,
      activeProducts: products.filter(p => p.isActive).length,
      availableProducts: products.filter(p => p.isAvailable).length,
      inStockProducts: products.filter(p => p.availableQuantity > 0).length,
      outOfStockProducts: products.filter(p => p.availableQuantity === 0).length,
      lowStockProducts: products.filter(p => {
        const stockStatus = productService.getStockStatus(p.inventory);
        return stockStatus.status === 'low' || stockStatus.status === 'critical';
      }).length,
      totalValue: products.reduce((sum, p) => sum + (p.basePrice * p.availableQuantity), 0),
      averagePrice: 0,
      categoryCounts: {},
      typeCounts: {}
    };

    // Calculate average price
    if (stats.totalProducts > 0) {
      stats.averagePrice = products.reduce((sum, p) => sum + p.basePrice, 0) / stats.totalProducts;
    }

    // Count by category
    products.forEach(product => {
      stats.categoryCounts[product.category] = (stats.categoryCounts[product.category] || 0) + 1;
    });

    // Count by type
    products.forEach(product => {
      stats.typeCounts[product.type] = (stats.typeCounts[product.type] || 0) + 1;
    });

    return stats;
  },

  /**
   * Generate SKU suggestion based on product data
   * @param {Object} productData - Product data
   * @returns {string} Suggested SKU
   */
  generateSKUSuggestion: (productData) => {
    if (!productData.name || !productData.category) return '';

    // Get first 3 characters of category
    const categoryCode = productData.category.substring(0, 3).toUpperCase();

    // Get first 3 characters of each word in name
    const nameWords = productData.name.trim().split(' ');
    const nameCode = nameWords
      .map(word => word.substring(0, 3).toUpperCase())
      .join('')
      .substring(0, 6);

    // Add timestamp for uniqueness
    const timestamp = Date.now().toString().slice(-4);

    return `${categoryCode}-${nameCode}-${timestamp}`;
  },

  /**
   * Validate image URL
   * @param {string} url - Image URL
   * @returns {boolean} Whether URL is valid
   */
  validateImageUrl: (url) => {
    if (!url) return true; // Optional field

    try {
      const urlObj = new URL(url);
      return urlObj.protocol === 'http:' || urlObj.protocol === 'https:';
    } catch (error) {
      return false;
    }
  },

  /**
   * Format product for display card
   * @param {Object} product - Product object
   * @returns {Object} Formatted product for UI display
   */
  formatProductForDisplay: (product) => {
    if (!product) return null;

    const availability = productService.getAvailabilityStatus(product);
    const stockStatus = productService.getStockStatus(product.inventory);
    const discountPercentage = productService.calculateDiscountPercentage(
      product.basePrice,
      product.discountedPrice
    );

    return {
      ...product,
      formattedPrice: productService.formatPrice(product.basePrice),
      formattedDiscountedPrice: product.discountedPrice
        ? productService.formatPrice(product.discountedPrice)
        : null,
      discountPercentage,
      hasDiscount: discountPercentage > 0,
      availability,
      stockStatus,
      categoryDisplay: productService.getCategoryDisplayName(product.category),
      typeDisplay: productService.getTypeDisplayName(product.type),
      isInStock: product.availableQuantity > 0,
      canOrder: availability.available && product.availableQuantity >= product.minOrderQuantity
    };
  }
};

export default productService;