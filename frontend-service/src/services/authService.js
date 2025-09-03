// src/services/authService.js
import { apiClient, API_BASE_URLS, clearAuthData } from './api.js';

const USER_BASE_URL = API_BASE_URLS.USER_SERVICE;

/**
 * Authentication Service - Handles all authentication-related API calls
 */
export const authService = {
  // ===============================
  // AUTHENTICATION ENDPOINTS
  // ===============================

  /**
   * Send OTP for login
   * @param {string} phone - Phone number in international format
   * @returns {Promise<Object>} OTP send response
   */
  sendLoginOTP: async (phone) => {
    try {
      const response = await apiClient.post(`${USER_BASE_URL}/auth/send-otp`, null, {
        params: { phone }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 429) {
        throw new Error('Too many OTP requests. Please wait before trying again.');
      }
      throw new Error(error.response?.data?.message || 'Failed to send OTP');
    }
  },

  /**
   * Login with phone and OTP
   * @param {Object} loginData - Login credentials
   * @param {string} loginData.phone - Phone number
   * @param {string} loginData.otp - OTP code
   * @param {string} loginData.deviceId - Unique device identifier
   * @param {string} loginData.deviceType - Device type
   * @param {string} loginData.fcmToken - Firebase Cloud Messaging token (optional)
   * @returns {Promise<Object>} Login response with tokens and user data
   */
  login: async (loginData) => {
    try {
      const response = await apiClient.post(`${USER_BASE_URL}/auth/login`, loginData);

      if (response.data.success && response.data.data) {
        const { sessionToken, refreshToken, accessToken, user } = response.data.data;

        // Store tokens and user data
        localStorage.setItem('authToken', sessionToken || accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        localStorage.setItem('userData', JSON.stringify(user));

        // Update last login
        if (user.id) {
          try {
            await this.updateLastLogin(user.id);
          } catch (error) {
            console.warn('Failed to update last login:', error);
          }
        }
      }

      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Login failed');
    }
  },

  /**
   * Logout user
   * @param {string} sessionToken - Current session token (optional)
   * @returns {Promise<void>}
   */
  logout: async (sessionToken) => {
    try {
      // If session token provided, deactivate it on server
      if (sessionToken) {
        await apiClient.patch(`${USER_BASE_URL}/sessions/deactivate`, null, {
          params: { sessionToken }
        });
      }
    } catch (error) {
      console.warn('Failed to deactivate session on server:', error);
    } finally {
      // Always clear local storage
      clearAuthData();
    }
  },

  /**
   * Update last login timestamp
   * @param {number} userId - User ID
   * @returns {Promise<Object>} Response
   */
  updateLastLogin: async (userId) => {
    try {
      const response = await apiClient.patch(`${USER_BASE_URL}/users/${userId}/last-login`);
      return response.data;
    } catch (error) {
      console.error('Failed to update last login:', error);
      return null;
    }
  },

  // ===============================
  // SESSION MANAGEMENT ENDPOINTS
  // ===============================

  /**
   * Create a new session
   * @param {Object} sessionData - Session creation data
   * @param {number} sessionData.userId - User ID
   * @param {string} sessionData.deviceId - Unique device identifier
   * @param {string} sessionData.deviceType - Device type
   * @param {string} sessionData.fcmToken - FCM token (optional)
   * @returns {Promise<Object>} Session data
   */
  createSession: async (sessionData) => {
    try {
      const response = await apiClient.post(`${USER_BASE_URL}/sessions/create`, null, {
        params: sessionData
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to create session');
    }
  },

  /**
   * Refresh session using refresh token
   * @param {string} refreshToken - Valid refresh token
   * @returns {Promise<Object>} New session data
   */
  refreshSession: async (refreshToken) => {
    try {
      const response = await apiClient.post(`${USER_BASE_URL}/sessions/refresh`, null, {
        params: { refreshToken }
      });

      if (response.data.success && response.data.data) {
        const { sessionToken, refreshToken: newRefreshToken } = response.data.data;

        // Update stored tokens
        localStorage.setItem('authToken', sessionToken);
        if (newRefreshToken) {
          localStorage.setItem('refreshToken', newRefreshToken);
        }
      }

      return response.data;
    } catch (error) {
      // If refresh fails, clear auth data
      if (error.response?.status === 401) {
        clearAuthData();
      }
      throw new Error(error.response?.data?.message || 'Failed to refresh session');
    }
  },

  /**
   * Validate session token
   * @param {string} sessionToken - Session token to validate
   * @returns {Promise<boolean>} Whether session is valid
   */
  validateSession: async (sessionToken) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/sessions/validate`, {
        params: { sessionToken }
      });
      return response.data.data;
    } catch (error) {
      return false;
    }
  },

  /**
   * Update last accessed time for session
   * @param {string} sessionToken - Session token
   * @returns {Promise<Object>} Response
   */
  updateLastAccessed: async (sessionToken) => {
    try {
      const response = await apiClient.patch(`${USER_BASE_URL}/sessions/update-access`, null, {
        params: { sessionToken }
      });
      return response.data;
    } catch (error) {
      console.warn('Failed to update last accessed:', error);
      return null;
    }
  },

  /**
   * Deactivate a specific session
   * @param {string} sessionToken - Session token to deactivate
   * @returns {Promise<Object>} Response
   */
  deactivateSession: async (sessionToken) => {
    try {
      const response = await apiClient.patch(`${USER_BASE_URL}/sessions/deactivate`, null, {
        params: { sessionToken }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to deactivate session');
    }
  },

  /**
   * Deactivate all sessions for a user
   * @param {number} userId - User ID
   * @returns {Promise<Object>} Response
   */
  deactivateAllSessions: async (userId) => {
    try {
      const response = await apiClient.patch(`${USER_BASE_URL}/sessions/deactivate-all/${userId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to deactivate all sessions');
    }
  },

  /**
   * Get active sessions for a user
   * @param {number} userId - User ID
   * @returns {Promise<Array>} List of active sessions
   */
  getUserActiveSessions: async (userId) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/sessions/user/${userId}/active`);
      return response.data.data || [];
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch active sessions');
    }
  },

  // ===============================
  // UTILITY METHODS
  // ===============================

  /**
   * Check if user is currently authenticated
   * @returns {boolean} Whether user is authenticated
   */
  isAuthenticated: () => {
    return !!localStorage.getItem('authToken');
  },

  /**
   * Get current user data from localStorage
   * @returns {Object|null} Current user data
   */
  getCurrentUser: () => {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
  },

  /**
   * Get current auth token
   * @returns {string|null} Current auth token
   */
  getAuthToken: () => {
    return localStorage.getItem('authToken');
  },

  /**
   * Get refresh token
   * @returns {string|null} Refresh token
   */
  getRefreshToken: () => {
    return localStorage.getItem('refreshToken');
  },

  /**
   * Clear all authentication data
   */
  clearAuthData: () => {
    clearAuthData();
  },

  /**
   * Generate a simple device ID (for demo purposes)
   * In production, use a more sophisticated method
   * @returns {string} Device ID
   */
  generateDeviceId: () => {
    return `device_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  },

  /**
   * Get device type
   * @returns {string} Device type
   */
  getDeviceType: () => {
    const userAgent = navigator.userAgent.toLowerCase();
    if (userAgent.includes('mobile')) return 'MOBILE';
    if (userAgent.includes('tablet')) return 'TABLET';
    return 'DESKTOP';
  }
};

export default authService;