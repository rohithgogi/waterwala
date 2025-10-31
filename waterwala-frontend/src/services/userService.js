import api from './api';

export const userService = {
  // Register new user
  register: async (userData) => {
    return api.post('/users/register', userData);
  },

  // Get user by ID
  getUserById: async (userId) => {
    return api.get(`/users/id/${userId}`);
  },

  // Update user
  updateUser: async (userId, updateData) => {
    return api.put(`/users/${userId}`, updateData);
  },

  // Verify email
  verifyEmail: async (userId) => {
    return api.patch(`/users/${userId}/verify-email`);
  },

  // Verify phone
  verifyPhone: async (userId) => {
    return api.patch(`/users/${userId}/verify-phone`);
  },

  // Check email exists
  checkEmailExists: async (email) => {
    return api.get(`/users/exists/email/${email}`);
  },

  // Check phone exists
  checkPhoneExists: async (phone) => {
    return api.get(`/users/exists/phone/${phone}`);
  }
};