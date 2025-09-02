// src/services/api.js
import axios from 'axios';

// Base URLs for your microservices
export const API_BASE_URLS = {
  USER_SERVICE: 'http://localhost:8081/api/v1',
  BUSINESS_SERVICE: 'http://localhost:8082/api/v1',
  PRODUCT_SERVICE: 'http://localhost:8083/api/v1',
  ORDER_SERVICE: 'http://localhost:8084/api/v1'
};

// Create axios instance with common config
const apiClient = axios.create({
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Request interceptor to add token to requests
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error('Request interceptor error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor to handle common errors
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Handle 401 Unauthorized
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const refreshToken = localStorage.getItem('refreshToken');
      if (refreshToken) {
        try {
          // Try to refresh the token
          const refreshResponse = await axios.post(
            `${API_BASE_URLS.USER_SERVICE}/sessions/refresh`,
            null,
            { params: { refreshToken } }
          );

          if (refreshResponse.data.success) {
            const { sessionToken, refreshToken: newRefreshToken } = refreshResponse.data.data;
            localStorage.setItem('authToken', sessionToken);
            localStorage.setItem('refreshToken', newRefreshToken);

            // Retry the original request with new token
            originalRequest.headers.Authorization = `Bearer ${sessionToken}`;
            return apiClient(originalRequest);
          }
        } catch (refreshError) {
          console.error('Token refresh failed:', refreshError);
          // Clear auth data and redirect to login
          localStorage.removeItem('authToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('userData');
          window.location.href = '/login';
        }
      } else {
        // No refresh token, redirect to login
        localStorage.removeItem('authToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('userData');
        window.location.href = '/login';
      }
    }

    return Promise.reject(error);
  }
);

export { apiClient };

// Helper function to check if user is authenticated
export const isAuthenticated = () => {
  return !!localStorage.getItem('authToken');
};

// Helper function to get current user data
export const getCurrentUser = () => {
  const userData = localStorage.getItem('userData');
  return userData ? JSON.parse(userData) : null;
};

// Helper function to clear auth data
export const clearAuthData = () => {
  localStorage.removeItem('authToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('userData');
  localStorage.removeItem('sessionId');
};