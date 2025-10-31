import { useState, useEffect } from 'react';
import { userService } from '../services/userService';
import toast from 'react-hot-toast';

export const useUser = (userId) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchUser = async () => {
    if (!userId) {
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      const response = await userService.getUserById(userId);
      setUser(response.data);
      setError(null);
    } catch (err) {
      setError(err.message);
      toast.error('Failed to fetch user details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUser();
  }, [userId]);

  const updateUser = async (updateData) => {
    try {
      const response = await userService.updateUser(userId, updateData);
      setUser(response.data);
      toast.success('Profile updated successfully');
      return response.data;
    } catch (err) {
      toast.error('Failed to update profile');
      throw err;
    }
  };

  const verifyEmail = async () => {
    try {
      await userService.verifyEmail(userId);
      setUser(prev => ({ ...prev, emailVerified: true }));
      toast.success('Email verified successfully');
    } catch (err) {
      toast.error('Email verification failed');
      throw err;
    }
  };

  const verifyPhone = async () => {
    try {
      await userService.verifyPhone(userId);
      setUser(prev => ({ ...prev, phoneVerified: true }));
      toast.success('Phone verified successfully');
    } catch (err) {
      toast.error('Phone verification failed');
      throw err;
    }
  };

  return {
    user,
    loading,
    error,
    refetch: fetchUser,
    updateUser,
    verifyEmail,
    verifyPhone
  };
};