import api from './api';

export const otpService = {
  // Send email verification OTP
  sendEmailVerificationOTP: async (email) => {
    return api.post('/otp/send/email-verification', null, { params: { email } });
  },

  // Send phone verification OTP
  sendPhoneVerificationOTP: async (phone) => {
    return api.post('/otp/send/phone-verification', null, { params: { phone } });
  },

  // Verify OTP
  verifyOTP: async (contact, otpCode, type) => {
    return api.post('/otp/verify', null, {
      params: { contact, otpCode, type }
    });
  },

  // Check OTP status
  checkOTPStatus: async (contact, type) => {
    return api.get('/otp/status', {
      params: { contact, type }
    });
  }
};