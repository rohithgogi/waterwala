import api from './api';

export const authService = {
  // Send OTP for login
  sendLoginOTP: async (phone) => {
    return api.post('/auth/send-otp', null, { params: { phone } });
  },

  // Login with OTP
  login: async (loginData) => {
    const response = await api.post('/auth/login', loginData);
    const { accessToken, refreshToken, sessionToken, user } = response.data;

    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('sessionToken', sessionToken);
    localStorage.setItem('user', JSON.stringify(user));

    return response;
  },

  // Logout
  logout: async () => {
    const sessionToken = localStorage.getItem('sessionToken');
    if (sessionToken) {
      try {
        await api.patch('/sessions/deactivate', null, { params: { sessionToken } });
      } catch (error) {
        console.error('Logout error:', error);
      }
    }
    localStorage.clear();
  },

  // Get current user
  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  // Check if authenticated
  isAuthenticated: () => {
    return !!localStorage.getItem('accessToken');
  }
};