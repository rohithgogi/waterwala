import React, { createContext, useContext, useState, useEffect } from 'react';
import { apiClient, API_BASE_URLS } from '../services/api';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  const [sessionToken, setSessionToken] = useState(null);

  // Initialize auth state on app load
  useEffect(() => {
    initializeAuth();
  }, []);

  const initializeAuth = async () => {
    try {
      const token = localStorage.getItem('authToken');
      const refreshToken = localStorage.getItem('refreshToken');
      const userData = localStorage.getItem('userData');

      if (token && userData) {
        const parsedUser = JSON.parse(userData);
        setUser(parsedUser);
        setSessionToken(token);
        setIsAuthenticated(true);

        // Validate token with backend
        const isValid = await validateSession(token);
        if (!isValid && refreshToken) {
          // Try to refresh the session
          const refreshed = await refreshSession(refreshToken);
          if (!refreshed) {
            logout();
          }
        } else if (!isValid) {
          logout();
        }
      }
    } catch (error) {
      console.error('Error initializing auth:', error);
      logout();
    } finally {
      setLoading(false);
    }
  };

  const login = async (phoneNumber, otp) => {
    try {
      setLoading(true);

      // Step 1: Login with OTP
      const loginResponse = await apiClient.post(`${API_BASE_URLS.USER_SERVICE}/auth/login`, {
        phone: phoneNumber,
        otp: otp
      });

      if (loginResponse.data.success) {
        const { user: userData, accessToken, refreshToken } = loginResponse.data.data;

        // Step 2: Create session
        const sessionResponse = await apiClient.post(`${API_BASE_URLS.USER_SERVICE}/sessions/create`, null, {
          params: {
            userId: userData.id,
            deviceId: generateDeviceId(),
            deviceType: getDeviceType(),
            fcmToken: null // Add FCM token if you implement push notifications
          }
        });

        if (sessionResponse.data.success) {
          const sessionData = sessionResponse.data.data;

          // Store auth data
          localStorage.setItem('authToken', sessionData.accessToken);
          localStorage.setItem('refreshToken', sessionData.refreshToken);
          localStorage.setItem('userData', JSON.stringify(userData));
          localStorage.setItem('sessionId', sessionData.sessionId);

          // Update state
          setUser(userData);
          setSessionToken(sessionData.accessToken);
          setIsAuthenticated(true);

          return { success: true, user: userData };
        }
      }

      return { success: false, message: 'Login failed' };
    } catch (error) {
      console.error('Login error:', error);
      return {
        success: false,
        message: error.response?.data?.message || 'Login failed. Please try again.'
      };
    } finally {
      setLoading(false);
    }
  };

  const sendOTP = async (phoneNumber) => {
    try {
      const response = await apiClient.post(`${API_BASE_URLS.USER_SERVICE}/auth/send-otp`, null, {
        params: { phone: phoneNumber }
      });

      return {
        success: response.data.success,
        message: response.data.message || 'OTP sent successfully'
      };
    } catch (error) {
      console.error('Send OTP error:', error);
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to send OTP'
      };
    }
  };

  const register = async (registrationData) => {
    try {
      setLoading(true);

      const response = await apiClient.post(`${API_BASE_URLS.USER_SERVICE}/users/register`, registrationData);

      if (response.data.success) {
        return {
          success: true,
          user: response.data.data,
          message: 'Registration successful'
        };
      }

      return { success: false, message: 'Registration failed' };
    } catch (error) {
      console.error('Registration error:', error);
      return {
        success: false,
        message: error.response?.data?.message || 'Registration failed'
      };
    } finally {
      setLoading(false);
    }
  };

  const refreshSession = async (refreshToken) => {
    try {
      const response = await apiClient.post(`${API_BASE_URLS.USER_SERVICE}/sessions/refresh`, null, {
        params: { refreshToken }
      });

      if (response.data.success) {
        const sessionData = response.data.data;

        // Update stored tokens
        localStorage.setItem('authToken', sessionData.accessToken);
        localStorage.setItem('refreshToken', sessionData.refreshToken);

        setSessionToken(sessionData.accessToken);
        return true;
      }

      return false;
    } catch (error) {
      console.error('Refresh session error:', error);
      return false;
    }
  };

  const validateSession = async (token) => {
    try {
      const response = await apiClient.get(`${API_BASE_URLS.USER_SERVICE}/sessions/validate`, {
        params: { sessionToken: token }
      });

      return response.data.success && response.data.data === true;
    } catch (error) {
      console.error('Validate session error:', error);
      return false;
    }
  };

  const logout = async () => {
    try {
      const token = localStorage.getItem('authToken');

      if (token) {
        // Deactivate session on backend
        await apiClient.patch(`${API_BASE_URLS.USER_SERVICE}/sessions/deactivate`, null, {
          params: { sessionToken: token }
        });
      }
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // Clear local storage and state
      localStorage.removeItem('authToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('userData');
      localStorage.removeItem('sessionId');

      setUser(null);
      setSessionToken(null);
      setIsAuthenticated(false);
    }
  };

  const updateLastAccessed = async () => {
    try {
      const token = localStorage.getItem('authToken');
      if (token) {
        await apiClient.patch(`${API_BASE_URLS.USER_SERVICE}/sessions/update-access`, null, {
          params: { sessionToken: token }
        });
      }
    } catch (error) {
      console.error('Update last accessed error:', error);
    }
  };

  // Helper functions
  const generateDeviceId = () => {
    // Generate a unique device ID (you can make this more sophisticated)
    let deviceId = localStorage.getItem('deviceId');
    if (!deviceId) {
      deviceId = 'web_' + Math.random().toString(36).substr(2, 9) + '_' + Date.now();
      localStorage.setItem('deviceId', deviceId);
    }
    return deviceId;
  };

  const getDeviceType = () => {
    const userAgent = navigator.userAgent;
    if (/Android/i.test(userAgent)) return 'Android';
    if (/iPhone|iPad|iPod/i.test(userAgent)) return 'iOS';
    if (/Windows/i.test(userAgent)) return 'Windows';
    if (/Mac/i.test(userAgent)) return 'Mac';
    return 'Web';
  };

  // Auto-update last accessed time every 5 minutes when user is active
  useEffect(() => {
    let interval;

    if (isAuthenticated) {
      interval = setInterval(() => {
        updateLastAccessed();
      }, 5 * 60 * 1000); // 5 minutes
    }

    return () => {
      if (interval) {
        clearInterval(interval);
      }
    };
  }, [isAuthenticated]);

  const value = {
    user,
    isAuthenticated,
    loading,
    sessionToken,
    login,
    logout,
    register,
    sendOTP,
    refreshSession,
    validateSession,
    updateLastAccessed
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};