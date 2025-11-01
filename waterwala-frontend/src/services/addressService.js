import api from './api';

export const addressService = {
  // Get all addresses for user
  getAllAddresses: async (userId) => {
    const response = await api.get(`/addresses/user/${userId}`);
    return { data: response.data }; // Extract data from StandardResponse
  },

  // Get default address
  getDefaultAddress: async (userId) => {
    const response = await api.get(`/addresses/user/${userId}/default`);
    return { data: response.data };
  },

  // Add new address
  addAddress: async (userId, addressData) => {
    const response = await api.post(`/addresses/user/${userId}`, addressData);
    return { data: response.data };
  },

  // Update address
  updateAddress: async (addressId, addressData) => {
    const response = await api.put(`/addresses/${addressId}`, addressData);
    return { data: response.data };
  },

  // Delete address
  deleteAddress: async (addressId) => {
    const response = await api.delete(`/addresses/${addressId}`);
    return { data: response.data };
  },

  // Set default address
  setDefaultAddress: async (addressId) => {
    const response = await api.patch(`/addresses/${addressId}/set-default`);
    return { data: response.data };
  },

  // Get address by ID
  getAddressById: async (addressId) => {
    const response = await api.get(`/addresses/${addressId}`);
    return { data: response.data };
  }
};