// src/services/addressService.js
import { apiClient, API_BASE_URLS } from './api.js';

const USER_BASE_URL = API_BASE_URLS.USER_SERVICE;

/**
 * Address Service - Handles all address-related API calls
 */
export const addressService = {
  // Address Types enum
  ADDRESS_TYPES: {
    HOME: 'HOME',
    OFFICE: 'OFFICE',
    OTHER: 'OTHER'
  },

  // ===============================
  // ADDRESS MANAGEMENT ENDPOINTS
  // ===============================

  /**
   * Get all addresses for a user
   * @param {number} userId - User ID
   * @returns {Promise<Array>} List of user addresses
   */
  getAllAddressesOfUser: async (userId) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/addresses/user/${userId}`);
      return response.data.data || [];
    } catch (error) {
      if (error.response?.status === 404) {
        return []; // User not found or no addresses
      }
      throw new Error(error.response?.data?.message || 'Failed to fetch addresses');
    }
  },

  /**
   * Get address by ID
   * @param {number} addressId - Address ID
   * @returns {Promise<Object>} Address data
   */
  getAddressById: async (addressId) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/addresses/${addressId}`);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Address not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to fetch address');
    }
  },

  /**
   * Get default address for a user
   * @param {number} userId - User ID
   * @returns {Promise<Object|null>} Default address data
   */
  getDefaultAddress: async (userId) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/addresses/user/${userId}/default`);
      return response.data.data;
    } catch (error) {
      if (error.response?.status === 404) {
        return null; // No default address found
      }
      throw new Error(error.response?.data?.message || 'Failed to fetch default address');
    }
  },

  /**
   * Add new address for user
   * @param {number} userId - User ID
   * @param {Object} addressData - Address data
   * @param {string} addressData.addressLine1 - Address line 1 (required, max 100 chars)
   * @param {string} addressData.addressLine2 - Address line 2 (optional, max 100 chars)
   * @param {string} addressData.landmark - Landmark (optional, max 100 chars)
   * @param {string} addressData.city - City (required, max 50 chars)
   * @param {string} addressData.state - State (required, max 50 chars)
   * @param {string} addressData.pincode - Pincode (required, 6 digits starting with 1-9)
   * @param {string} addressData.country - Country (optional)
   * @param {string} addressData.type - Address type (HOME, OFFICE, OTHER)
   * @param {boolean} addressData.isDefault - Whether this is default address
   * @param {number} addressData.latitude - Latitude (optional)
   * @param {number} addressData.longitude - Longitude (optional)
   * @returns {Promise<Object>} Created address data
   */
  addAddress: async (userId, addressData) => {
    try {
      // Validate required fields
      const validatedData = addressService.validateAddressData(addressData);

      const response = await apiClient.post(`${USER_BASE_URL}/addresses/user/${userId}`, validatedData);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('User not found');
      }
      if (error.response?.status === 400) {
        throw new Error(error.response?.data?.message || 'Invalid address data');
      }
      throw new Error(error.response?.data?.message || 'Failed to add address');
    }
  },

  /**
   * Update existing address
   * @param {number} addressId - Address ID
   * @param {Object} addressData - Updated address data
   * @returns {Promise<Object>} Updated address data
   */
  updateAddress: async (addressId, addressData) => {
    try {
      // Validate data
      const validatedData = addressService.validateAddressData(addressData);

      const response = await apiClient.put(`${USER_BASE_URL}/addresses/${addressId}`, validatedData);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Address not found');
      }
      if (error.response?.status === 400) {
        throw new Error(error.response?.data?.message || 'Invalid address data');
      }
      throw new Error(error.response?.data?.message || 'Failed to update address');
    }
  },

  /**
   * Delete address
   * @param {number} addressId - Address ID
   * @returns {Promise<Object>} Delete response
   */
  deleteAddress: async (addressId) => {
    try {
      const response = await apiClient.delete(`${USER_BASE_URL}/addresses/${addressId}`);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Address not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to delete address');
    }
  },

  /**
   * Set address as default
   * @param {number} addressId - Address ID
   * @returns {Promise<Object>} Updated address data
   */
  setDefaultAddress: async (addressId) => {
    try {
      const response = await apiClient.patch(`${USER_BASE_URL}/addresses/${addressId}/set-default`);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error('Address not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to set default address');
    }
  },

  // ===============================
  // UTILITY METHODS
  // ===============================

  /**
   * Validate address data
   * @param {Object} addressData - Address data to validate
   * @returns {Object} Validated address data
   */
  validateAddressData: (addressData) => {
    const errors = [];
    const validated = { ...addressData };

    // Required fields validation
    if (!validated.addressLine1 || validated.addressLine1.trim().length === 0) {
      errors.push('Address line 1 is required');
    } else if (validated.addressLine1.length > 100) {
      errors.push('Address line 1 must be less than 100 characters');
    }

    if (!validated.city || validated.city.trim().length === 0) {
      errors.push('City is required');
    } else if (validated.city.length > 50) {
      errors.push('City must be less than 50 characters');
    }

    if (!validated.state || validated.state.trim().length === 0) {
      errors.push('State is required');
    } else if (validated.state.length > 50) {
      errors.push('State must be less than 50 characters');
    }

    if (!validated.pincode || validated.pincode.trim().length === 0) {
      errors.push('Pincode is required');
    } else if (!addressService.validatePincode(validated.pincode)) {
      errors.push('Invalid pincode format');
    }

    if (!validated.type || !Object.values(addressService.ADDRESS_TYPES).includes(validated.type)) {
      errors.push('Valid address type is required (HOME, OFFICE, OTHER)');
    }

    // Optional fields validation
    if (validated.addressLine2 && validated.addressLine2.length > 100) {
      errors.push('Address line 2 must be less than 100 characters');
    }

    if (validated.landmark && validated.landmark.length > 100) {
      errors.push('Landmark must be less than 100 characters');
    }

    // Set defaults
    if (!validated.country) {
      validated.country = 'India';
    }

    if (validated.isDefault === undefined) {
      validated.isDefault = false;
    }

    if (errors.length > 0) {
      throw new Error(errors.join(', '));
    }

    return validated;
  },

  /**
   * Validate Indian pincode format
   * @param {string} pincode - Pincode to validate
   * @returns {boolean} Whether pincode is valid
   */
  validatePincode: (pincode) => {
    const pincodeRegex = /^[1-9][0-9]{5}$/;
    return pincodeRegex.test(pincode);
  },

  /**
   * Format address for display
   * @param {Object} address - Address object
   * @returns {string} Formatted address string
   */
  formatAddress: (address) => {
    if (!address) return '';

    const parts = [
      address.addressLine1,
      address.addressLine2,
      address.landmark,
      address.city,
      address.state,
      address.pincode,
      address.country
    ].filter(part => part && part.trim().length > 0);

    return parts.join(', ');
  },

  /**
   * Get formatted address for display (short version)
   * @param {Object} address - Address object
   * @returns {string} Short formatted address
   */
  formatShortAddress: (address) => {
    if (!address) return '';

    const parts = [
      address.addressLine1,
      address.city,
      address.pincode
    ].filter(part => part && part.trim().length > 0);

    return parts.join(', ');
  },

  /**
   * Get address type display name
   * @param {string} type - Address type
   * @returns {string} Display name
   */
  getTypeDisplayName: (type) => {
    switch (type) {
      case addressService.ADDRESS_TYPES.HOME:
        return 'Home';
      case addressService.ADDRESS_TYPES.OFFICE:
        return 'Office';
      case addressService.ADDRESS_TYPES.OTHER:
        return 'Other';
      default:
        return type;
    }
  },

  /**
   * Get address type icon
   * @param {string} type - Address type
   * @returns {string} Icon name (for icon libraries like Lucide)
   */
  getTypeIcon: (type) => {
    switch (type) {
      case addressService.ADDRESS_TYPES.HOME:
        return 'home';
      case addressService.ADDRESS_TYPES.OFFICE:
        return 'building';
      case addressService.ADDRESS_TYPES.OTHER:
        return 'map-pin';
      default:
        return 'map-pin';
    }
  },

  /**
   * Create a new address object with default values
   * @param {Object} overrides - Values to override defaults
   * @returns {Object} New address object
   */
  createNewAddress: (overrides = {}) => {
    return {
      addressLine1: '',
      addressLine2: '',
      landmark: '',
      city: '',
      state: '',
      pincode: '',
      country: 'India',
      type: addressService.ADDRESS_TYPES.HOME,
      isDefault: false,
      latitude: null,
      longitude: null,
      ...overrides
    };
  },

  /**
   * Sort addresses by priority (default first, then by type)
   * @param {Array} addresses - Array of addresses
   * @returns {Array} Sorted addresses
   */
  sortAddresses: (addresses) => {
    if (!Array.isArray(addresses)) return [];

    return [...addresses].sort((a, b) => {
      // Default addresses first
      if (a.isDefault && !b.isDefault) return -1;
      if (!a.isDefault && b.isDefault) return 1;

      // Then sort by type priority
      const typePriority = {
        [addressService.ADDRESS_TYPES.HOME]: 1,
        [addressService.ADDRESS_TYPES.OFFICE]: 2,
        [addressService.ADDRESS_TYPES.OTHER]: 3
      };

      const aPriority = typePriority[a.type] || 3;
      const bPriority = typePriority[b.type] || 3;

      if (aPriority !== bPriority) {
        return aPriority - bPriority;
      }

      // Finally sort by creation date (newest first)
      if (a.createdAt && b.createdAt) {
        return new Date(b.createdAt) - new Date(a.createdAt);
      }

      return 0;
    });
  },

  /**
   * Get coordinates from address (geocoding)
   * Note: This is a placeholder. In production, you would integrate with
   * Google Maps Geocoding API or similar service
   * @param {Object} address - Address object
   * @returns {Promise<Object>} Coordinates {latitude, longitude}
   */
  getCoordinates: async (address) => {
    try {
      // This is a placeholder implementation
      // In production, integrate with Google Maps Geocoding API
      const fullAddress = addressService.formatAddress(address);

      // For demo purposes, return random coordinates within India
      // Replace this with actual geocoding service call
      const latitude = 12.9716 + (Math.random() - 0.5) * 20; // Rough India bounds
      const longitude = 77.5946 + (Math.random() - 0.5) * 20;

      return { latitude, longitude };
    } catch (error) {
      console.warn('Failed to get coordinates:', error);
      return { latitude: null, longitude: null };
    }
  },

  /**
   * Calculate distance between two addresses (if coordinates are available)
   * @param {Object} address1 - First address
   * @param {Object} address2 - Second address
   * @returns {number|null} Distance in kilometers, null if coordinates unavailable
   */
  calculateDistance: (address1, address2) => {
    if (!address1?.latitude || !address1?.longitude ||
        !address2?.latitude || !address2?.longitude) {
      return null;
    }

    const R = 6371; // Earth's radius in kilometers
    const dLat = (address2.latitude - address1.latitude) * Math.PI / 180;
    const dLon = (address2.longitude - address1.longitude) * Math.PI / 180;

    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
              Math.cos(address1.latitude * Math.PI / 180) * Math.cos(address2.latitude * Math.PI / 180) *
              Math.sin(dLon/2) * Math.sin(dLon/2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    const distance = R * c;

    return Math.round(distance * 100) / 100; // Round to 2 decimal places
  }
};

export default addressService;