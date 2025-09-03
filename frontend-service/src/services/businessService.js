// src/services/businessService.js
import { apiClient, API_BASE_URLS } from './api.js';

const BUSINESS_BASE_URL = API_BASE_URLS.BUSINESS_SERVICE;

/**
 * Business Service - Handles all business-related API calls
 */
export const businessService = {
  // Business Status enum
  BUSINESS_STATUS: {
    PENDING_VERIFICATION: 'PENDING_VERIFICATION',
    VERIFIED: 'VERIFIED',
    SUSPENDED: 'SUSPENDED',
    DEACTIVATED: 'DEACTIVATED'
  },

  // Verification Status enum
  VERIFICATION_STATUS: {
    PENDING: 'PENDING',
    UNDER_REVIEW: 'UNDER_REVIEW',
    APPROVED: 'APPROVED',
    REJECTED: 'REJECTED',
    RESUBMISSION_REQUIRED: 'RESUBMISSION_REQUIRED'
  },

  // Service Types enum
  SERVICE_TYPES: {
    WATER_DELIVERY: 'WATER_DELIVERY',
    TANK_INSTALLATION: 'TANK_INSTALLATION',
    TANK_CLEANING: 'TANK_CLEANING',
    TANK_MAINTENANCE: 'TANK_MAINTENANCE',
    WATER_TESTING: 'WATER_TESTING',
    PURIFIER_INSTALLATION: 'PURIFIER_INSTALLATION',
    PURIFIER_MAINTENANCE: 'PURIFIER_MAINTENANCE',
    BULK_SUPPLY: 'BULK_SUPPLY',
    EMERGENCY_SERVICE: 'EMERGENCY_SERVICE'
  },

  // Days of Week enum
  DAYS_OF_WEEK: {
    SUNDAY: 'SUNDAY',
    MONDAY: 'MONDAY',
    TUESDAY: 'TUESDAY',
    WEDNESDAY: 'WEDNESDAY',
    THURSDAY: 'THURSDAY',
    FRIDAY: 'FRIDAY',
    SATURDAY: 'SATURDAY'
  },

  // ===============================
  // BUSINESS REGISTRATION & PROFILE
  // ===============================

  /**
   * Register a new business
   * @param {Object} businessData - Business registration data
   * @param {number} businessData.userId - User ID
   * @param {string} businessData.businessName - Business name (max 255 chars)
   * @param {string} businessData.businessType - Business type (max 100 chars)
   * @param {string} businessData.businessRegistrationNumber - Registration number (10-20 chars)
   * @param {string} businessData.gstNumber - GST number (15 chars)
   * @param {string} businessData.contactPersonName - Contact person name (max 255 chars)
   * @param {string} businessData.contactEmail - Contact email
   * @param {string} businessData.contactPhone - Contact phone (10 digits starting with 6-9)
   * @param {string} businessData.description - Business description (max 1000 chars)
   * @param {string} businessData.logoUrl - Logo URL
   * @param {Object} businessData.address - Business address
   * @param {Array} businessData.services - Business services
   * @param {Array} businessData.operatingHours - Operating hours
   * @returns {Promise<Object>} Registration response
   */
  registerBusiness: async (businessData) => {
    try {
      // Validate required fields
      const validatedData = businessService.validateBusinessRegistrationData(businessData);

      const response = await apiClient.post(`${BUSINESS_BASE_URL}/businesses/register`, validatedData);
      return response.data;
    } catch (error) {
      if (error.response?.status === 400) {
        throw new Error(error.response?.data?.message || 'Invalid business data');
      }
      if (error.response?.status === 409) {
        throw new Error('Business already exists');
      }
      throw new Error(error.response?.data?.message || 'Failed to register business');
    }
  },

  /**
   * Get business profile by ID
   * @param {number} businessId - Business ID
   * @returns {Promise<Object>} Business profile data
   */
  getBusinessProfile: async (businessId) => {
    try {
      const response = await apiClient.get(`${BUSINESS_BASE_URL}/businesses/${businessId}`);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Business not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to fetch business profile');
    }
  },

  /**
   * Get business by user ID
   * @param {number} userId - User ID
   * @returns {Promise<Object>} Business profile data
   */
  getBusinessByUserId: async (userId) => {
    try {
      const response = await apiClient.get(`${BUSINESS_BASE_URL}/businesses/user/${userId}`);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Business not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to fetch business profile');
    }
  },

  /**
   * Update business profile
   * @param {number} businessId - Business ID
   * @param {Object} updateData - Update data
   * @param {string} updateData.businessName - Business name (max 255 chars)
   * @param {string} updateData.description - Description (max 1000 chars)
   * @param {string} updateData.contactPersonName - Contact person name (max 255 chars)
   * @param {string} updateData.contactEmail - Contact email
   * @param {string} updateData.contactPhone - Contact phone (10 digits)
   * @param {string} updateData.logoUrl - Logo URL
   * @param {boolean} updateData.isAvailable - Availability status
   * @returns {Promise<Object>} Updated business data
   */
  updateBusinessProfile: async (businessId, updateData) => {
    try {
      // Add User-Id header for authorization
      const currentUser = businessService.getCurrentUser();
      if (!currentUser) {
        throw new Error('User not authenticated');
      }

      const response = await apiClient.put(`${BUSINESS_BASE_URL}/businesses/${businessId}`, updateData, {
        headers: {
          'X-User-Id': currentUser.id
        }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Business not found');
      }
      if (error.response?.status === 403) {
        throw new Error('Unauthorized access');
      }
      if (error.response?.status === 400) {
        throw new Error(error.response?.data?.message || 'Invalid update data');
      }
      throw new Error(error.response?.data?.message || 'Failed to update business profile');
    }
  },

  /**
   * Deactivate business
   * @param {number} businessId - Business ID
   * @returns {Promise<Object>} Deactivation response
   */
  deactivateBusiness: async (businessId) => {
    try {
      const currentUser = businessService.getCurrentUser();
      if (!currentUser) {
        throw new Error('User not authenticated');
      }

      const response = await apiClient.delete(`${BUSINESS_BASE_URL}/businesses/${businessId}`, {
        headers: {
          'X-User-Id': currentUser.id
        }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Business not found');
      }
      if (error.response?.status === 403) {
        throw new Error('Unauthorized access');
      }
      throw new Error(error.response?.data?.message || 'Failed to deactivate business');
    }
  },

  // ===============================
  // BUSINESS SEARCH & DISCOVERY
  // ===============================

  /**
   * Search businesses by various criteria
   * @param {Object} searchParams - Search parameters
   * @param {string} searchParams.pincode - Pincode filter
   * @param {string} searchParams.city - City filter
   * @param {string} searchParams.state - State filter
   * @param {string} searchParams.serviceType - Service type filter
   * @param {string} searchParams.keyword - Keyword search
   * @param {number} searchParams.latitude - Latitude for location-based search
   * @param {number} searchParams.longitude - Longitude for location-based search
   * @param {number} searchParams.radius - Radius in km for location search
   * @param {number} searchParams.minRating - Minimum rating filter
   * @param {string} searchParams.businessType - Business type filter
   * @returns {Promise<Array>} List of businesses
   */
  searchBusinesses: async (searchParams = {}) => {
    try {
      const {
        pincode,
        city,
        state,
        serviceType,
        keyword,
        latitude,
        longitude,
        radius,
        minRating,
        businessType
      } = searchParams;

      const response = await apiClient.get(`${BUSINESS_BASE_URL}/businesses/search`, {
        params: {
          arg0: pincode,
          arg1: city,
          arg2: state,
          arg3: serviceType,
          arg4: keyword,
          arg5: latitude,
          arg6: longitude,
          arg7: radius,
          arg8: minRating,
          arg9: businessType
        }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to search businesses');
    }
  },

  /**
   * Get featured businesses
   * @param {Object} pageParams - Pagination parameters
   * @param {number} pageParams.page - Page number (default: 0)
   * @param {number} pageParams.size - Page size (default: 10)
   * @param {string} pageParams.sortBy - Sort field (default: 'averageRating')
   * @param {string} pageParams.sortDirection - Sort direction (default: 'desc')
   * @returns {Promise<Object>} Paginated featured businesses
   */
  getFeaturedBusinesses: async (pageParams = {}) => {
    try {
      const {
        page = 0,
        size = 10,
        sortBy = 'averageRating',
        sortDirection = 'desc'
      } = pageParams;

      const response = await apiClient.get(`${BUSINESS_BASE_URL}/businesses/featured`, {
        params: {
          arg0: page,
          arg1: size,
          arg2: sortBy,
          arg3: sortDirection
        }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch featured businesses');
    }
  },

  // ===============================
  // BUSINESS VERIFICATION
  // ===============================

  /**
   * Verify business (Admin only)
   * @param {number} businessId - Business ID
   * @param {Object} verificationData - Verification data
   * @param {string} verificationData.status - Verification status
   * @param {string} verificationData.comments - Verification comments
   * @param {string} verificationData.rejectionReason - Rejection reason (if rejected)
   * @returns {Promise<Object>} Verification response
   */
  verifyBusiness: async (businessId, verificationData) => {
    try {
      const response = await apiClient.post(`${BUSINESS_BASE_URL}/businesses/${businessId}/verify`, verificationData);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Business not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to verify business');
    }
  },

  // ===============================
  // BUSINESS STATISTICS
  // ===============================

  /**
   * Update business rating
   * @param {number} businessId - Business ID
   * @param {number} averageRating - Average rating
   * @param {number} reviewCount - Review count
   * @returns {Promise<Object>} Update response
   */
  updateBusinessRating: async (businessId, averageRating, reviewCount) => {
    try {
      const response = await apiClient.patch(`${BUSINESS_BASE_URL}/businesses/${businessId}/rating`, null, {
        params: {
          arg1: averageRating,
          arg2: reviewCount
        }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Business not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to update business rating');
    }
  },

  /**
   * Update order statistics
   * @param {number} businessId - Business ID
   * @param {number} totalOrders - Total orders count
   * @param {number} completedOrders - Completed orders count
   * @returns {Promise<Object>} Update response
   */
  updateOrderStats: async (businessId, totalOrders, completedOrders) => {
    try {
      const response = await apiClient.patch(`${BUSINESS_BASE_URL}/businesses/${businessId}/orders`, null, {
        params: {
          arg1: totalOrders,
          arg2: completedOrders
        }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Business not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to update order statistics');
    }
  },

  // ===============================
  // BUSINESS OWNERSHIP
  // ===============================

  /**
   * Check if user is business owner
   * @param {number} businessId - Business ID
   * @returns {Promise<boolean>} Whether user is owner
   */
  isBusinessOwner: async (businessId) => {
    try {
      const currentUser = businessService.getCurrentUser();
      if (!currentUser) {
        return false;
      }

      const response = await apiClient.get(`${BUSINESS_BASE_URL}/businesses/${businessId}/owner`, {
        headers: {
          'X-User-Id': currentUser.id
        }
      });
      return response.data;
    } catch (error) {
      return false;
    }
  },

  // ===============================
  // UTILITY METHODS
  // ===============================

  /**
   * Get current user from localStorage
   * @returns {Object|null} Current user data
   */
  getCurrentUser: () => {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
  },

  /**
   * Validate business registration data
   * @param {Object} businessData - Business data to validate
   * @returns {Object} Validated business data
   */
  validateBusinessRegistrationData: (businessData) => {
    const errors = [];
    const validated = { ...businessData };

    // Required fields validation
    if (!validated.userId) {
      errors.push('User ID is required');
    }

    if (!validated.businessName || validated.businessName.trim().length === 0) {
      errors.push('Business name is required');
    } else if (validated.businessName.length > 255) {
      errors.push('Business name must be less than 255 characters');
    }

    if (!validated.businessType || validated.businessType.trim().length === 0) {
      errors.push('Business type is required');
    } else if (validated.businessType.length > 100) {
      errors.push('Business type must be less than 100 characters');
    }

    if (!validated.businessRegistrationNumber || !businessService.validateRegistrationNumber(validated.businessRegistrationNumber)) {
      errors.push('Valid business registration number is required (10-20 alphanumeric characters)');
    }

    if (!validated.gstNumber || !businessService.validateGSTNumber(validated.gstNumber)) {
      errors.push('Valid GST number is required');
    }

    if (!validated.contactPersonName || validated.contactPersonName.trim().length === 0) {
      errors.push('Contact person name is required');
    } else if (validated.contactPersonName.length > 255) {
      errors.push('Contact person name must be less than 255 characters');
    }

    if (!validated.contactEmail || !businessService.validateEmail(validated.contactEmail)) {
      errors.push('Valid contact email is required');
    }

    if (!validated.contactPhone || !businessService.validatePhone(validated.contactPhone)) {
      errors.push('Valid contact phone is required (10 digits starting with 6-9)');
    }

    if (!validated.address || !businessService.validateAddress(validated.address)) {
      errors.push('Valid business address is required');
    }

    if (!validated.services || !Array.isArray(validated.services) || validated.services.length === 0) {
      errors.push('At least one business service is required');
    }

    if (!validated.operatingHours || !Array.isArray(validated.operatingHours) || validated.operatingHours.length === 0) {
      errors.push('Operating hours are required');
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
   * Validate business registration number
   * @param {string} regNumber - Registration number
   * @returns {boolean} Whether valid
   */
  validateRegistrationNumber: (regNumber) => {
    const regNumberRegex = /^[A-Z0-9]{10,20}$/;
    return regNumberRegex.test(regNumber);
  },

  /**
   * Validate GST number
   * @param {string} gstNumber - GST number
   * @returns {boolean} Whether valid
   */
  validateGSTNumber: (gstNumber) => {
    const gstRegex = /^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$/;
    return gstRegex.test(gstNumber);
  },

  /**
   * Validate phone number
   * @param {string} phone - Phone number
   * @returns {boolean} Whether valid
   */
  validatePhone: (phone) => {
    const phoneRegex = /^[6-9]\d{9}$/;
    return phoneRegex.test(phone);
  },

  /**
   * Validate email format
   * @param {string} email - Email address
   * @returns {boolean} Whether valid
   */
  validateEmail: (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  },

  /**
   * Validate Indian pincode
   * @param {string} pincode - Pincode
   * @returns {boolean} Whether valid
   */
  validatePincode: (pincode) => {
    const pincodeRegex = /^[1-9][0-9]{5}$/;
    return pincodeRegex.test(pincode);
  },

  /**
   * Validate business address
   * @param {Object} address - Address object
   * @returns {boolean} Whether valid
   */
  validateAddress: (address) => {
    if (!address) return false;

    return !!(
      address.addressLine1 &&
      address.addressLine1.trim().length > 0 &&
      address.city &&
      address.city.trim().length > 0 &&
      address.state &&
      address.state.trim().length > 0 &&
      address.pincode &&
      businessService.validatePincode(address.pincode)
    );
  },

  /**
   * Get business status display name
   * @param {string} status - Business status
   * @returns {string} Display name
   */
  getStatusDisplayName: (status) => {
    switch (status) {
      case businessService.BUSINESS_STATUS.PENDING_VERIFICATION:
        return 'Pending Verification';
      case businessService.BUSINESS_STATUS.VERIFIED:
        return 'Verified';
      case businessService.BUSINESS_STATUS.SUSPENDED:
        return 'Suspended';
      case businessService.BUSINESS_STATUS.DEACTIVATED:
        return 'Deactivated';
      default:
        return status;
    }
  },

  /**
   * Get verification status display name
   * @param {string} status - Verification status
   * @returns {string} Display name
   */
  getVerificationStatusDisplayName: (status) => {
    switch (status) {
      case businessService.VERIFICATION_STATUS.PENDING:
        return 'Pending';
      case businessService.VERIFICATION_STATUS.UNDER_REVIEW:
        return 'Under Review';
      case businessService.VERIFICATION_STATUS.APPROVED:
        return 'Approved';
      case businessService.VERIFICATION_STATUS.REJECTED:
        return 'Rejected';
      case businessService.VERIFICATION_STATUS.RESUBMISSION_REQUIRED:
        return 'Resubmission Required';
      default:
        return status;
    }
  },

  /**
   * Get service type display name
   * @param {string} serviceType - Service type
   * @returns {string} Display name
   */
  getServiceTypeDisplayName: (serviceType) => {
    const serviceNames = {
      [businessService.SERVICE_TYPES.WATER_DELIVERY]: 'Water Delivery',
      [businessService.SERVICE_TYPES.TANK_INSTALLATION]: 'Tank Installation',
      [businessService.SERVICE_TYPES.TANK_CLEANING]: 'Tank Cleaning',
      [businessService.SERVICE_TYPES.TANK_MAINTENANCE]: 'Tank Maintenance',
      [businessService.SERVICE_TYPES.WATER_TESTING]: 'Water Testing',
      [businessService.SERVICE_TYPES.PURIFIER_INSTALLATION]: 'Purifier Installation',
      [businessService.SERVICE_TYPES.PURIFIER_MAINTENANCE]: 'Purifier Maintenance',
      [businessService.SERVICE_TYPES.BULK_SUPPLY]: 'Bulk Supply',
      [businessService.SERVICE_TYPES.EMERGENCY_SERVICE]: 'Emergency Service'
    };

    return serviceNames[serviceType] || serviceType;
  },

  /**
   * Get day display name
   * @param {string} day - Day of week
   * @returns {string} Display name
   */
  getDayDisplayName: (day) => {
    const dayNames = {
      [businessService.DAYS_OF_WEEK.SUNDAY]: 'Sunday',
      [businessService.DAYS_OF_WEEK.MONDAY]: 'Monday',
      [businessService.DAYS_OF_WEEK.TUESDAY]: 'Tuesday',
      [businessService.DAYS_OF_WEEK.WEDNESDAY]: 'Wednesday',
      [businessService.DAYS_OF_WEEK.THURSDAY]: 'Thursday',
      [businessService.DAYS_OF_WEEK.FRIDAY]: 'Friday',
      [businessService.DAYS_OF_WEEK.SATURDAY]: 'Saturday'
    };

    return dayNames[day] || day;
  },

  /**
   * Format business address
   * @param {Object} address - Address object
   * @returns {string} Formatted address
   */
  formatBusinessAddress: (address) => {
    if (!address) return '';

    const parts = [
      address.addressLine1,
      address.addressLine2,
      address.city,
      address.state,
      address.pincode
    ].filter(part => part && part.trim().length > 0);

    return parts.join(', ');
  },

  /**
   * Format operating hours for display
   * @param {Array} operatingHours - Array of operating hours
   * @returns {Object} Formatted operating hours by day
   */
  formatOperatingHours: (operatingHours) => {
    if (!Array.isArray(operatingHours)) return {};

    const formatted = {};

    operatingHours.forEach(hour => {
      const dayName = businessService.getDayDisplayName(hour.dayOfWeek);

      if (!hour.isOpen) {
        formatted[dayName] = 'Closed';
      } else if (hour.is24Hours) {
        formatted[dayName] = '24 Hours';
      } else {
        const openTime = businessService.formatTime(hour.openTime);
        const closeTime = businessService.formatTime(hour.closeTime);
        formatted[dayName] = `${openTime} - ${closeTime}`;
      }
    });

    return formatted;
  },

  /**
   * Format time for display
   * @param {Object} timeObj - Time object with hour, minute
   * @returns {string} Formatted time
   */
  formatTime: (timeObj) => {
    if (!timeObj) return '';

    const hour = timeObj.hour || 0;
    const minute = timeObj.minute || 0;

    const period = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour === 0 ? 12 : hour > 12 ? hour - 12 : hour;

    return `${displayHour}:${minute.toString().padStart(2, '0')} ${period}`;
  },

  /**
   * Calculate business rating stars
   * @param {number} rating - Average rating
   * @returns {Object} Star rating info
   */
  calculateStars: (rating) => {
    if (!rating || rating < 0) {
      return { full: 0, half: 0, empty: 5, display: '0.0' };
    }

    const fullStars = Math.floor(rating);
    const hasHalfStar = rating - fullStars >= 0.5;
    const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);

    return {
      full: fullStars,
      half: hasHalfStar ? 1 : 0,
      empty: emptyStars,
      display: rating.toFixed(1)
    };
  },

  /**
   * Get business status color for UI
   * @param {string} status - Business status
   * @returns {string} Color class or code
   */
  getStatusColor: (status) => {
    switch (status) {
      case businessService.BUSINESS_STATUS.VERIFIED:
        return 'text-green-600';
      case businessService.BUSINESS_STATUS.PENDING_VERIFICATION:
        return 'text-yellow-600';
      case businessService.BUSINESS_STATUS.SUSPENDED:
        return 'text-red-600';
      case businessService.BUSINESS_STATUS.DEACTIVATED:
        return 'text-gray-600';
      default:
        return 'text-gray-600';
    }
  },

  /**
   * Filter businesses by criteria
   * @param {Array} businesses - Array of businesses
   * @param {Object} filters - Filter criteria
   * @returns {Array} Filtered businesses
   */
  filterBusinesses: (businesses, filters = {}) => {
    if (!Array.isArray(businesses)) return [];

    return businesses.filter(business => {
      // Filter by service type
      if (filters.serviceType) {
        const hasService = business.services?.some(service =>
          service.serviceType === filters.serviceType
        );
        if (!hasService) return false;
      }

      // Filter by minimum rating
      if (filters.minRating && business.averageRating < filters.minRating) {
        return false;
      }

      // Filter by availability
      if (filters.availableOnly && !business.isAvailable) {
        return false;
      }

      // Filter by verification status
      if (filters.verifiedOnly && business.verificationStatus !== businessService.VERIFICATION_STATUS.APPROVED) {
        return false;
      }

      // Filter by business type
      if (filters.businessType && business.businessType !== filters.businessType) {
        return false;
      }

      return true;
    });
  },

  /**
   * Sort businesses by criteria
   * @param {Array} businesses - Array of businesses
   * @param {string} sortBy - Sort field
   * @param {string} sortOrder - Sort order (asc/desc)
   * @returns {Array} Sorted businesses
   */
  sortBusinesses: (businesses, sortBy = 'averageRating', sortOrder = 'desc') => {
    if (!Array.isArray(businesses)) return [];

    return [...businesses].sort((a, b) => {
      let valueA = a[sortBy];
      let valueB = b[sortBy];

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
   * Create new business service object
   * @param {Object} overrides - Values to override defaults
   * @returns {Object} New service object
   */
  createNewService: (overrides = {}) => {
    return {
      serviceType: businessService.SERVICE_TYPES.WATER_DELIVERY,
      serviceName: '',
      description: '',
      isActive: true,
      basePrice: 0,
      estimatedDuration: 60, // minutes
      ...overrides
    };
  },

  /**
   * Create new operating hours object
   * @param {string} dayOfWeek - Day of week
   * @param {Object} overrides - Values to override defaults
   * @returns {Object} New operating hours object
   */
  createNewOperatingHours: (dayOfWeek, overrides = {}) => {
    return {
      dayOfWeek,
      isOpen: true,
      openTime: { hour: 9, minute: 0 },
      closeTime: { hour: 18, minute: 0 },
      is24Hours: false,
      ...overrides
    };
  },

  /**
   * Generate default operating hours for all days
   * @param {Object} defaultHours - Default hours to apply
   * @returns {Array} Operating hours for all days
   */
  generateDefaultOperatingHours: (defaultHours = {}) => {
    return Object.values(businessService.DAYS_OF_WEEK).map(day =>
      businessService.createNewOperatingHours(day, defaultHours)
    );
  }
};

export default businessService;