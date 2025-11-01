import api from './api';

export const userService = {
  // Register new user
  register: async (userData) => {
    const response = await api.post('/users/register', userData);
    return { data: response.data };
  },

  // Get user by ID
  getUserById: async (userId) => {
    const response = await api.get(`/users/id/${userId}`);
    return { data: response.data };
  },

  // Update user
  updateUser: async (userId, updateData) => {
    const response = await api.put(`/users/${userId}`, updateData);
    return { data: response.data };
  },

  // Verify email
  verifyEmail: async (userId) => {
    const response = await api.patch(`/users/${userId}/verify-email`);
    return { data: response.data };
  },

  // Verify phone
  verifyPhone: async (userId) => {
    const response = await api.patch(`/users/${userId}/verify-phone`);
    return { data: response.data };
  },

  // Check email exists
  checkEmailExists: async (email) => {
    const response = await api.get(`/users/exists/email/${email}`);
    return { data: response.data };
  },

  // Check phone exists
  checkPhoneExists: async (phone) => {
    const response = await api.get(`/users/exists/phone/${phone}`);
    return { data: response.data };
  }
};