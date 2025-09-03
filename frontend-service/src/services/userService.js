// src/services/userService.js
import { apiClient, API_BASE_URLS } from './api.js';

const USER_BASE_URL = API_BASE_URLS.USER_SERVICE;

/**
 * User Service - Handles all user-related API calls
 */
export const userService = {
  // ===============================
  // USER MANAGEMENT ENDPOINTS
  // ===============================

  /**
   * Register a new user
   * @param {Object} userData - User registration data
   * @param {string} userData.email - User email
   * @param {string} userData.phone - User phone (10 digits)
   * @param {string} userData.firstName - First name (2-50 chars)
   * @param {string} userData.lastName - Last name (2-50 chars)
   * @param {string} userData.role - User role (CUSTOMER, BUSINESS_OWNER, ADMIN)
   * @returns {Promise<Object>} Registration response
   */
  registerUser: async (userData) => {
    try {
      const response = await apiClient.post(`${USER_BASE_URL}/users/register`, userData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'User registration failed');
    }
  },

  /**
   * Get user by ID
   * @param {number} userId - User ID
   * @returns {Promise<Object>} User data
   */
  getUserById: async (userId) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/users/id/${userId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch user');
    }
  },

  /**
   * Get user by email
   * @param {string} email - User email
   * @returns {Promise<Object>} User data
   */
  getUserByEmail: async (email) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/users/email/${email}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch user by email');
    }
  },

  /**
   * Get user by phone
   * @param {string} phone - User phone number
   * @returns {Promise<Object>} User data
   */
  getUserByPhone: async (phone) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/users/phone/${phone}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch user by phone');
    }
  },

  /**
   * Update user profile
   * @param {number} userId - User ID
   * @param {Object} updateData - Update data
   * @param {string} updateData.firstName - First name (2-50 chars)
   * @param {string} updateData.lastName - Last name (2-50 chars)
   * @param {string} updateData.profileImageURL - Profile image URL
   * @returns {Promise<Object>} Updated user data
   */
  updateUser: async (userId, updateData) => {
    try {
      const response = await apiClient.put(`${USER_BASE_URL}/users/${userId}`, updateData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to update user');
    }
  },

  /**
   * Get all users (Admin only, paginated)
   * @param {Object} pageParams - Pagination parameters
   * @param {number} pageParams.page - Page number (0-based)
   * @param {number} pageParams.size - Page size
   * @param {Array<string>} pageParams.sort - Sort parameters
   * @returns {Promise<Object>} Paginated user data
   */
  getAllUsers: async (pageParams = { page: 0, size: 10, sort: [] }) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/users`, {
        params: pageParams
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch users');
    }
  },

  /**
   * Get users by status
   * @param {string} status - User status (ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION)
   * @param {Object} pageParams - Pagination parameters
   * @returns {Promise<Object>} Paginated user data
   */
  getUsersByStatus: async (status, pageParams = { page: 0, size: 10, sort: [] }) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/users/status/${status}`, {
        params: pageParams
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch users by status');
    }
  },

  /**
   * Get users by role
   * @param {string} role - User role (CUSTOMER, BUSINESS_OWNER, ADMIN)
   * @param {Object} pageParams - Pagination parameters
   * @returns {Promise<Object>} Paginated user data
   */
  getUsersByRole: async (role, pageParams = { page: 0, size: 10, sort: [] }) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/users/role/${role}`, {
        params: pageParams
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch users by role');
    }
  },

  /**
   * Check if email exists
   * @param {string} email - Email to check
   * @returns {Promise<boolean>} Whether email exists
   */
  checkEmailExists: async (email) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/users/exists/email/${email}`);
      return response.data.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to check email existence');
    }
  },

  /**
   * Check if phone exists
   * @param {string} phone - Phone to check
   * @returns {Promise<boolean>} Whether phone exists
   */
  checkPhoneExists: async (phone) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/users/exists/phone/${phone}`);
      return response.data.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to check phone existence');
    }
  },

  /**
   * Update user status
   * @param {number} userId - User ID
   * @param {string} status - New status (ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION)
   * @returns {Promise<Object>} Updated user data
   */
  updateUserStatus: async (userId, status) => {
    try {
      const response = await apiClient.patch(`${USER_BASE_URL}/users/${userId}/status`, null, {
        params: { status }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to update user status');
    }
  },

  /**
   * Verify user's phone number
   * @param {number} userId - User ID
   * @returns {Promise<Object>} Updated user data
   */
  verifyPhone: async (userId) => {
    try {
      const response = await apiClient.patch(`${USER_BASE_URL}/users/${userId}/verify-phone`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to verify phone');
    }
  },

  /**
   * Verify user's email
   * @param {number} userId - User ID
   * @returns {Promise<Object>} Updated user data
   */
  verifyEmail: async (userId) => {
    try {
      const response = await apiClient.patch(`${USER_BASE_URL}/users/${userId}/verify-email`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to verify email');
    }
  },

  /**
   * Update user's last login timestamp
   * @param {number} userId - User ID
   * @returns {Promise<Object>} Updated user data
   */
  updateLastLogin: async (userId) => {
    try {
      const response = await apiClient.patch(`${USER_BASE_URL}/users/${userId}/last-login`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to update last login');
    }
  },

  /**
   * Validate user for microservice communication
   * @param {number} userId - User ID to validate
   * @returns {Promise<Object>} User validation data
   */
  validateUser: async (userId) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/users/${userId}/validate`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to validate user');
    }
  }
};

export default userService;