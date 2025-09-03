// src/services/otpService.js
import { apiClient, API_BASE_URLS } from './api.js';

const USER_BASE_URL = API_BASE_URLS.USER_SERVICE;

/**
 * OTP Service - Handles all OTP-related API calls
 */
export const otpService = {
  // OTP Types enum
  OTP_TYPES: {
    LOGIN: 'LOGIN',
    REGISTRATION: 'REGISTRATION',
    PASSWORD_RESET: 'PASSWORD_RESET',
    PHONE_VERIFICATION: 'PHONE_VERIFICATION',
    EMAIL_VERIFICATION: 'EMAIL_VERIFICATION'
  },

  // OTP Status enum
  OTP_STATUS: {
    PENDING: 'PENDING',
    VERIFIED: 'VERIFIED',
    EXPIRED: 'EXPIRED',
    FAILED: 'FAILED'
  },

  // ===============================
  // OTP SENDING ENDPOINTS
  // ===============================

  /**
   * Send phone verification OTP
   * @param {string} phone - Phone number in international format
   * @returns {Promise<Object>} OTP response
   */
  sendPhoneVerificationOTP: async (phone) => {
    try {
      const response = await apiClient.post(`${USER_BASE_URL}/otp/send/phone-verification`, null, {
        params: { phone }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 429) {
        throw new Error('Too many OTP requests. Please wait before trying again.');
      }
      if (error.response?.status === 400) {
        throw new Error('Invalid phone number format.');
      }
      throw new Error(error.response?.data?.message || 'Failed to send phone verification OTP');
    }
  },

  /**
   * Send email verification OTP
   * @param {string} email - Email address
   * @returns {Promise<Object>} OTP response
   */
  sendEmailVerificationOTP: async (email) => {
    try {
      const response = await apiClient.post(`${USER_BASE_URL}/otp/send/email-verification`, null, {
        params: { email }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 429) {
        throw new Error('Too many OTP requests. Please wait before trying again.');
      }
      if (error.response?.status === 400) {
        throw new Error('Invalid email format.');
      }
      throw new Error(error.response?.data?.message || 'Failed to send email verification OTP');
    }
  },

  /**
   * Send password reset OTP
   * @param {string} phone - Registered phone number
   * @returns {Promise<Object>} OTP response
   */
  sendPasswordResetOTP: async (phone) => {
    try {
      const response = await apiClient.post(`${USER_BASE_URL}/otp/send/password-reset`, null, {
        params: { phone }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 429) {
        throw new Error('Too many OTP requests. Please wait before trying again.');
      }
      if (error.response?.status === 404) {
        throw new Error('Phone number not registered.');
      }
      throw new Error(error.response?.data?.message || 'Failed to send password reset OTP');
    }
  },

  // ===============================
  // OTP VERIFICATION ENDPOINTS
  // ===============================

  /**
   * Verify OTP code
   * @param {Object} verificationData - OTP verification data
   * @param {string} verificationData.contact - Contact (email or phone)
   * @param {string} verificationData.otpCode - 6-digit OTP code
   * @param {string} verificationData.type - OTP type
   * @returns {Promise<Object>} Verification response
   */
  verifyOTP: async (verificationData) => {
    try {
      const { contact, otpCode, type } = verificationData;
      const response = await apiClient.post(`${USER_BASE_URL}/otp/verify`, null, {
        params: { contact, otpCode, type }
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 400) {
        throw new Error('Invalid OTP code or OTP has expired.');
      }
      throw new Error(error.response?.data?.message || 'OTP verification failed');
    }
  },

  /**
   * Check OTP verification status
   * @param {string} contact - Contact (email or phone)
   * @param {string} type - OTP type
   * @returns {Promise<boolean>} Whether OTP is verified
   */
  checkOTPStatus: async (contact, type) => {
    try {
      const response = await apiClient.get(`${USER_BASE_URL}/otp/status`, {
        params: { contact, type }
      });
      return response.data.data;
    } catch (error) {
      if (error.response?.status === 404) {
        return false; // No OTP record found
      }
      throw new Error(error.response?.data?.message || 'Failed to check OTP status');
    }
  },

  // ===============================
  // CONVENIENCE METHODS
  // ===============================

  /**
   * Send and verify phone verification OTP (complete flow)
   * @param {string} phone - Phone number
   * @param {string} otpCode - OTP code (when verifying)
   * @param {boolean} isVerification - Whether this is verification step
   * @returns {Promise<Object>} Response
   */
  handlePhoneVerification: async (phone, otpCode = null, isVerification = false) => {
    if (!isVerification) {
      // Send OTP
      return await otpService.sendPhoneVerificationOTP(phone);
    } else {
      // Verify OTP
      return await otpService.verifyOTP({
        contact: phone,
        otpCode,
        type: otpService.OTP_TYPES.PHONE_VERIFICATION
      });
    }
  },

  /**
   * Send and verify email verification OTP (complete flow)
   * @param {string} email - Email address
   * @param {string} otpCode - OTP code (when verifying)
   * @param {boolean} isVerification - Whether this is verification step
   * @returns {Promise<Object>} Response
   */
  handleEmailVerification: async (email, otpCode = null, isVerification = false) => {
    if (!isVerification) {
      // Send OTP
      return await otpService.sendEmailVerificationOTP(email);
    } else {
      // Verify OTP
      return await otpService.verifyOTP({
        contact: email,
        otpCode,
        type: otpService.OTP_TYPES.EMAIL_VERIFICATION
      });
    }
  },

  /**
   * Handle password reset flow
   * @param {string} phone - Phone number
   * @param {string} otpCode - OTP code (when verifying)
   * @param {boolean} isVerification - Whether this is verification step
   * @returns {Promise<Object>} Response
   */
  handlePasswordReset: async (phone, otpCode = null, isVerification = false) => {
    if (!isVerification) {
      // Send OTP
      return await otpService.sendPasswordResetOTP(phone);
    } else {
      // Verify OTP
      return await otpService.verifyOTP({
        contact: phone,
        otpCode,
        type: otpService.OTP_TYPES.PASSWORD_RESET
      });
    }
  },

  /**
   * Validate OTP code format
   * @param {string} otpCode - OTP code to validate
   * @returns {boolean} Whether OTP format is valid
   */
  validateOTPFormat: (otpCode) => {
    return /^\d{6}$/.test(otpCode);
  },

  /**
   * Validate phone number format (Indian format)
   * @param {string} phone - Phone number to validate
   * @returns {boolean} Whether phone format is valid
   */
  validatePhoneFormat: (phone) => {
    return /^[6-9]\d{9}$/.test(phone);
  },

  /**
   * Validate email format
   * @param {string} email - Email to validate
   * @returns {boolean} Whether email format is valid
   */
  validateEmailFormat: (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  },

  /**
   * Format phone number for display
   * @param {string} phone - Phone number
   * @returns {string} Formatted phone number
   */
  formatPhoneNumber: (phone) => {
    if (!phone) return '';

    // Remove any non-digits
    const cleaned = phone.replace(/\D/g, '');

    // Format as +91 XXXXX XXXXX for Indian numbers
    if (cleaned.length === 10) {
      return `+91 ${cleaned.slice(0, 5)} ${cleaned.slice(5)}`;
    }

    return phone;
  },

  /**
   * Get OTP expiry time in minutes based on type
   * @param {string} type - OTP type
   * @returns {number} Expiry time in minutes
   */
  getOTPExpiryTime: (type) => {
    switch (type) {
      case otpService.OTP_TYPES.LOGIN:
        return 5; // 5 minutes
      case otpService.OTP_TYPES.PASSWORD_RESET:
        return 5; // 5 minutes
      case otpService.OTP_TYPES.PHONE_VERIFICATION:
      case otpService.OTP_TYPES.EMAIL_VERIFICATION:
        return 10; // 10 minutes
      case otpService.OTP_TYPES.REGISTRATION:
        return 10; // 10 minutes
      default:
        return 5;
    }
  },

  /**
   * Calculate remaining time for OTP
   * @param {string} expiresAt - ISO timestamp of expiry
   * @returns {number} Remaining time in seconds
   */
  getRemainingTime: (expiresAt) => {
    if (!expiresAt) return 0;

    const expiryTime = new Date(expiresAt).getTime();
    const currentTime = new Date().getTime();
    const remainingMs = expiryTime - currentTime;

    return Math.max(0, Math.floor(remainingMs / 1000));
  },

  /**
   * Format remaining time for display
   * @param {number} seconds - Remaining seconds
   * @returns {string} Formatted time (MM:SS)
   */
  formatRemainingTime: (seconds) => {
    if (seconds <= 0) return '00:00';

    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;

    return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }
};

export default otpService;