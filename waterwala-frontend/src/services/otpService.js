import api from './api';

export const otpService = {
  // Send email verification OTP
  sendEmailVerificationOTP: async (email) => {
    const response = await api.post('/otp/send/email-verification', null, { params: { email } });
    return { data: response.data };
  },

  // Send phone verification OTP
  sendPhoneVerificationOTP: async (phone) => {
    const response = await api.post('/otp/send/phone-verification', null, { params: { phone } });
    return { data: response.data };
  },

  // Verify OTP
  verifyOTP: async (contact, otpCode, type) => {
    const response = await api.post('/otp/verify', null, {
      params: { contact, otpCode, type }
    });
    return { data: response.data };
  },

  // Check OTP status
  checkOTPStatus: async (contact, type) => {
    const response = await api.get('/otp/status', {
      params: { contact, type }
    });
    return { data: response.data };
  }
};