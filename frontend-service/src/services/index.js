// src/services/index.js
// Central exports for all services

// Base API configuration
export { apiClient, API_BASE_URLS, isAuthenticated, getCurrentUser, clearAuthData } from './api.js';

// User Service - User management operations
export { userService, default as userServiceDefault } from './userService.js';

// Authentication Service - Login, logout, session management
export { authService, default as authServiceDefault } from './authService.js';

// OTP Service - OTP sending and verification
export { otpService, default as otpServiceDefault } from './otpService.js';

// Address Service - Address management
export { addressService, default as addressServiceDefault } from './addressService.js';

// Session Service - Session management and monitoring
export { sessionService, default as sessionServiceDefault } from './sessionService.js';

// Convenience object with all services
export const services = {
  user: userService,
  auth: authService,
  otp: otpService,
  address: addressService,
  session: sessionService
};

// Default export
export default services;