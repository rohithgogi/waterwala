import axios from 'axios';

// Base URLs for your microservices
const API_BASE_URLS = {
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

// Add token to requests if available
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export { API_BASE_URLS, apiClient };