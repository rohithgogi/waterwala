import api from './api';

export const addressService = {
  // Get all addresses for user
  getAllAddresses: async (userId) => {
    return api.get(`/addresses/user/${userId}`);
  },

  // Get default address
  getDefaultAddress: async (userId) => {
    return api.get(`/addresses/user/${userId}/default`);
  },

  // Add new address
  addAddress: async (userId, addressData) => {
    return api.post(`/addresses/user/${userId}`, addressData);
  },

  // Update address
  updateAddress: async (addressId, addressData) => {
    return api.put(`/addresses/${addressId}`, addressData);
  },

  // Delete address
  deleteAddress: async (addressId) => {
    return api.delete(`/addresses/${addressId}`);
  },

  // Set default address
  setDefaultAddress: async (addressId) => {
    return api.patch(`/addresses/${addressId}/set-default`);
  },

  // Get address by ID
  getAddressById: async (addressId) => {
    return api.get(`/addresses/${addressId}`);
  }
};