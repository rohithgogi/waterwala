export const validators = {
  // Email validation
  email: (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email) return 'Email is required';
    if (!emailRegex.test(email)) return 'Invalid email format';
    return null;
  },

  // Phone validation (Indian format)
  phone: (phone) => {
    const phoneRegex = /^[6-9]\d{9}$/;
    if (!phone) return 'Phone number is required';
    if (!phoneRegex.test(phone)) return 'Invalid phone number. Must be 10 digits starting with 6-9';
    return null;
  },

  // Name validation
  name: (name, fieldName = 'Name') => {
    if (!name) return `${fieldName} is required`;
    if (name.length < 2) return `${fieldName} must be at least 2 characters`;
    if (name.length > 50) return `${fieldName} must not exceed 50 characters`;
    return null;
  },

  // OTP validation
  otp: (otp) => {
    const otpRegex = /^\d{6}$/;
    if (!otp) return 'OTP is required';
    if (!otpRegex.test(otp)) return 'OTP must be 6 digits';
    return null;
  },

  // Pincode validation (Indian)
  pincode: (pincode) => {
    const pincodeRegex = /^[1-9][0-9]{5}$/;
    if (!pincode) return 'Pincode is required';
    if (!pincodeRegex.test(pincode)) return 'Invalid pincode format';
    return null;
  },

  // Required field
  required: (value, fieldName = 'Field') => {
    if (!value || (typeof value === 'string' && !value.trim())) {
      return `${fieldName} is required`;
    }
    return null;
  }
};
