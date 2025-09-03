// src/services/sessionService.js
import { apiClient, API_BASE_URLS } from './api.js';

const USER_BASE_URL = API_BASE_URLS.USER_SERVICE;

/**
 * Session Service - Handles all session-related API calls
 */
export const sessionService = {
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
      if (error.response?.status === 404) {
        throw new Error('User not found');
      }
      if (error.response?.status === 400) {
        throw new Error('Invalid session creation parameters');
      }
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
      return response.data;
    } catch (error) {
      if (error.response?.status === 401) {
        throw new Error('Invalid or expired refresh token');
      }
      if (error.response?.status === 404) {
        throw new Error('Session not found');
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
      if (error.response?.status === 404) {
        throw new Error('Session not found');
      }
      if (error.response?.status === 401) {
        throw new Error('Invalid session token');
      }
      throw new Error(error.response?.data?.message || 'Failed to update last accessed time');
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
      if (error.response?.status === 404) {
        throw new Error('Session not found');
      }
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
      if (error.response?.status === 404) {
        throw new Error('User not found');
      }
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
      if (error.response?.status === 404) {
        throw new Error('User not found');
      }
      throw new Error(error.response?.data?.message || 'Failed to fetch active sessions');
    }
  },

  // ===============================
  // UTILITY METHODS
  // ===============================

  /**
   * Generate a device ID for the current browser/device
   * @returns {string} Device ID
   */
  generateDeviceId: () => {
    // Try to get existing device ID from localStorage
    let deviceId = localStorage.getItem('deviceId');

    if (!deviceId) {
      // Generate new device ID
      const timestamp = Date.now();
      const random = Math.random().toString(36).substr(2, 9);
      const userAgent = navigator.userAgent.slice(-10).replace(/[^a-zA-Z0-9]/g, '');

      deviceId = `web_${timestamp}_${random}_${userAgent}`;
      localStorage.setItem('deviceId', deviceId);
    }

    return deviceId;
  },

  /**
   * Get device type based on user agent
   * @returns {string} Device type
   */
  getDeviceType: () => {
    const userAgent = navigator.userAgent.toLowerCase();

    if (userAgent.includes('mobile')) return 'MOBILE';
    if (userAgent.includes('tablet') || userAgent.includes('ipad')) return 'TABLET';
    return 'DESKTOP';
  },

  /**
   * Get browser information
   * @returns {Object} Browser info
   */
  getBrowserInfo: () => {
    const userAgent = navigator.userAgent;
    let browser = 'Unknown';
    let version = 'Unknown';

    if (userAgent.includes('Chrome')) {
      browser = 'Chrome';
      const match = userAgent.match(/Chrome\/(\d+)/);
      version = match ? match[1] : 'Unknown';
    } else if (userAgent.includes('Firefox')) {
      browser = 'Firefox';
      const match = userAgent.match(/Firefox\/(\d+)/);
      version = match ? match[1] : 'Unknown';
    } else if (userAgent.includes('Safari') && !userAgent.includes('Chrome')) {
      browser = 'Safari';
      const match = userAgent.match(/Version\/(\d+)/);
      version = match ? match[1] : 'Unknown';
    } else if (userAgent.includes('Edge')) {
      browser = 'Edge';
      const match = userAgent.match(/Edge\/(\d+)/);
      version = match ? match[1] : 'Unknown';
    }

    return { browser, version };
  },

  /**
   * Get OS information
   * @returns {string} Operating system
   */
  getOperatingSystem: () => {
    const userAgent = navigator.userAgent;

    if (userAgent.includes('Windows')) return 'Windows';
    if (userAgent.includes('Mac')) return 'macOS';
    if (userAgent.includes('Linux')) return 'Linux';
    if (userAgent.includes('Android')) return 'Android';
    if (userAgent.includes('iOS')) return 'iOS';

    return 'Unknown';
  },

  /**
   * Format session data for display
   * @param {Object} session - Session object
   * @returns {Object} Formatted session data
   */
  formatSessionForDisplay: (session) => {
    if (!session) return null;

    return {
      ...session,
      deviceTypeDisplay: sessionService.getDeviceTypeDisplay(session.deviceType),
      isCurrentSession: sessionService.isCurrentSession(session.sessionToken),
      timeLeft: sessionService.getTimeRemaining(session.expiresAt),
      lastAccessedFormatted: sessionService.formatLastAccessed(session.lastAccessedAt),
      createdAtFormatted: sessionService.formatDate(session.createdAt)
    };
  },

  /**
   * Get device type display name
   * @param {string} deviceType - Device type
   * @returns {string} Display name
   */
  getDeviceTypeDisplay: (deviceType) => {
    switch (deviceType) {
      case 'DESKTOP': return 'Desktop';
      case 'MOBILE': return 'Mobile';
      case 'TABLET': return 'Tablet';
      default: return deviceType;
    }
  },

  /**
   * Check if session is the current session
   * @param {string} sessionToken - Session token to check
   * @returns {boolean} Whether this is the current session
   */
  isCurrentSession: (sessionToken) => {
    const currentToken = localStorage.getItem('authToken');
    return currentToken === sessionToken;
  },

  /**
   * Get remaining time for session
   * @param {string} expiresAt - ISO timestamp of expiry
   * @returns {Object} Time remaining info
   */
  getTimeRemaining: (expiresAt) => {
    if (!expiresAt) return { expired: true, timeLeft: 0, formatted: 'Expired' };

    const expiryTime = new Date(expiresAt).getTime();
    const currentTime = new Date().getTime();
    const remainingMs = expiryTime - currentTime;

    if (remainingMs <= 0) {
      return { expired: true, timeLeft: 0, formatted: 'Expired' };
    }

    const hours = Math.floor(remainingMs / (1000 * 60 * 60));
    const minutes = Math.floor((remainingMs % (1000 * 60 * 60)) / (1000 * 60));

    let formatted;
    if (hours > 0) {
      formatted = `${hours}h ${minutes}m`;
    } else if (minutes > 0) {
      formatted = `${minutes}m`;
    } else {
      const seconds = Math.floor(remainingMs / 1000);
      formatted = `${seconds}s`;
    }

    return {
      expired: false,
      timeLeft: remainingMs,
      formatted,
      hours,
      minutes
    };
  },

  /**
   * Format last accessed time
   * @param {string} lastAccessedAt - ISO timestamp
   * @returns {string} Formatted time
   */
  formatLastAccessed: (lastAccessedAt) => {
    if (!lastAccessedAt) return 'Never';

    const date = new Date(lastAccessedAt);
    const now = new Date();
    const diff = now - date;

    // Less than 1 minute
    if (diff < 60000) {
      return 'Just now';
    }

    // Less than 1 hour
    if (diff < 3600000) {
      const minutes = Math.floor(diff / 60000);
      return `${minutes} minute${minutes > 1 ? 's' : ''} ago`;
    }

    // Less than 24 hours
    if (diff < 86400000) {
      const hours = Math.floor(diff / 3600000);
      return `${hours} hour${hours > 1 ? 's' : ''} ago`;
    }

    // More than 24 hours - show date
    return sessionService.formatDate(lastAccessedAt);
  },

  /**
   * Format date for display
   * @param {string} dateString - ISO date string
   * @returns {string} Formatted date
   */
  formatDate: (dateString) => {
    if (!dateString) return 'Unknown';

    const date = new Date(dateString);
    const options = {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    };

    return date.toLocaleDateString('en-US', options);
  },

  /**
   * Check if session is about to expire (within 5 minutes)
   * @param {string} expiresAt - ISO timestamp of expiry
   * @returns {boolean} Whether session is about to expire
   */
  isSessionAboutToExpire: (expiresAt) => {
    if (!expiresAt) return true;

    const expiryTime = new Date(expiresAt).getTime();
    const currentTime = new Date().getTime();
    const remainingMs = expiryTime - currentTime;

    // Consider about to expire if less than 5 minutes remaining
    return remainingMs < (5 * 60 * 1000);
  },

  /**
   * Auto-refresh session if about to expire
   * @returns {Promise<boolean>} Whether refresh was successful
   */
  autoRefreshIfNeeded: async () => {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) return false;

      // Get current session info (you might need to store expiry time locally)
      const sessionExpiry = localStorage.getItem('sessionExpiry');
      if (!sessionExpiry) return false;

      if (sessionService.isSessionAboutToExpire(sessionExpiry)) {
        const response = await sessionService.refreshSession(refreshToken);

        if (response.success && response.data) {
          const { sessionToken, refreshToken: newRefreshToken, expiresAt } = response.data;

          localStorage.setItem('authToken', sessionToken);
          if (newRefreshToken) {
            localStorage.setItem('refreshToken', newRefreshToken);
          }
          if (expiresAt) {
            localStorage.setItem('sessionExpiry', expiresAt);
          }

          return true;
        }
      }

      return false;
    } catch (error) {
      console.warn('Auto-refresh failed:', error);
      return false;
    }
  },

  /**
   * Start session monitoring (call this when app starts)
   * @param {Function} onExpired - Callback when session expires
   * @param {Function} onAboutToExpire - Callback when session is about to expire
   * @returns {Function} Cleanup function to stop monitoring
   */
  startSessionMonitoring: (onExpired, onAboutToExpire) => {
    let warningShown = false;

    const checkSession = () => {
      const sessionExpiry = localStorage.getItem('sessionExpiry');
      if (!sessionExpiry) return;

      const timeRemaining = sessionService.getTimeRemaining(sessionExpiry);

      if (timeRemaining.expired) {
        if (onExpired) onExpired();
        return;
      }

      // Warn when 5 minutes remaining
      if (timeRemaining.timeLeft < (5 * 60 * 1000) && !warningShown) {
        warningShown = true;
        if (onAboutToExpire) onAboutToExpire(timeRemaining);
      }

      // Reset warning if session was refreshed
      if (timeRemaining.timeLeft > (10 * 60 * 1000)) {
        warningShown = false;
      }
    };

    // Check every minute
    const interval = setInterval(checkSession, 60000);

    // Check immediately
    checkSession();

    // Return cleanup function
    return () => {
      clearInterval(interval);
    };
  },

  /**
   * Clean up expired sessions from localStorage
   */
  cleanupExpiredSessions: () => {
    const sessionExpiry = localStorage.getItem('sessionExpiry');
    if (sessionExpiry) {
      const timeRemaining = sessionService.getTimeRemaining(sessionExpiry);
      if (timeRemaining.expired) {
        localStorage.removeItem('authToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('sessionExpiry');
        localStorage.removeItem('userData');
      }
    }
  }
};

export default sessionService;