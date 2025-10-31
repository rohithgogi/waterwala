import { useState, useEffect } from 'react';
import { addressService } from '../services/addressService';
import toast from 'react-hot-toast';

export const useAddresses = (userId) => {
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchAddresses = async () => {
    if (!userId) {
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      const response = await addressService.getAllAddresses(userId);
      setAddresses(response.data);
      setError(null);
    } catch (err) {
      setError(err.message);
      toast.error('Failed to fetch addresses');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAddresses();
  }, [userId]);

  const addAddress = async (addressData) => {
    try {
      const response = await addressService.addAddress(userId, addressData);
      setAddresses(prev => [...prev, response.data]);
      toast.success('Address added successfully');
      return response.data;
    } catch (err) {
      toast.error('Failed to add address');
      throw err;
    }
  };

  const updateAddress = async (addressId, addressData) => {
    try {
      const response = await addressService.updateAddress(addressId, addressData);
      setAddresses(prev =>
        prev.map(addr => addr.id === addressId ? response.data : addr)
      );
      toast.success('Address updated successfully');
      return response.data;
    } catch (err) {
      toast.error('Failed to update address');
      throw err;
    }
  };

  const deleteAddress = async (addressId) => {
    try {
      await addressService.deleteAddress(addressId);
      setAddresses(prev => prev.filter(addr => addr.id !== addressId));
      toast.success('Address deleted successfully');
    } catch (err) {
      toast.error('Failed to delete address');
      throw err;
    }
  };

  const setDefaultAddress = async (addressId) => {
    try {
      await addressService.setDefaultAddress(addressId);
      setAddresses(prev =>
        prev.map(addr => ({
          ...addr,
          isDefault: addr.id === addressId
        }))
      );
      toast.success('Default address updated');
    } catch (err) {
      toast.error('Failed to set default address');
      throw err;
    }
  };

  const defaultAddress = addresses.find(addr => addr.isDefault);

  return {
    addresses,
    defaultAddress,
    loading,
    error,
    refetch: fetchAddresses,
    addAddress,
    updateAddress,
    deleteAddress,
    setDefaultAddress
  };
};