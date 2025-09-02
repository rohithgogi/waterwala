import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { Droplets, Phone, Key, ArrowRight, ArrowLeft, Eye, EyeOff, Clock } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useNotification } from '../../context/NotificationContext';

const Login = () => {
  const [step, setStep] = useState(1); // 1: Phone, 2: OTP
  const [phoneNumber, setPhoneNumber] = useState('');
  const [otpTimer, setOtpTimer] = useState(0);
  const [canResendOtp, setCanResendOtp] = useState(false);

  const navigate = useNavigate();
  const { login, sendOTP, loading } = useAuth();
  const { showSuccess, showError, showInfo } = useNotification();

  // Phone number form
  const phoneForm = useForm({
    defaultValues: {
      phone: ''
    }
  });

  // OTP form
  const otpForm = useForm({
    defaultValues: {
      otp: ''
    }
  });

  // OTP Timer Effect
  useEffect(() => {
    let interval;
    if (otpTimer > 0) {
      interval = setInterval(() => {
        setOtpTimer(prev => {
          if (prev <= 1) {
            setCanResendOtp(true);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [otpTimer]);

  // Format timer display
  const formatTimer = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  // Handle phone number submission
  const handlePhoneSubmit = async (data) => {
      const formattedPhone = formatPhoneNumber(data.phone);

      if (!isValidPhoneNumber(formattedPhone)) {
        showError('Invalid Phone Number', 'Please enter a valid Indian mobile number');
        return;
      }

      const result = await sendOTP(formattedPhone);

      if (result.success) {
        setPhoneNumber(formattedPhone);
        setStep(2);
        setOtpTimer(300); // 5 minutes
        setCanResendOtp(false);
        showSuccess('OTP Sent', `Verification code sent to +91${formattedPhone}`);
      } else {
        showError('Failed to Send OTP', result.message);
      }
    };

  // Handle OTP submission
  const handleOtpSubmit = async (data) => {
    if (data.otp.length !== 6) {
      showError('Invalid OTP', 'Please enter a 6-digit OTP');
      return;
    }

    const result = await login(phoneNumber, data.otp);

    if (result.success) {
      showSuccess('Login Successful', 'Welcome to Waterwala!');
      navigate('/dashboard');
    } else {
      showError('Login Failed', result.message);
    }
  };

  // Resend OTP
  const handleResendOtp = async () => {
    const result = await sendOTP(phoneNumber);

    if (result.success) {
      setOtpTimer(300);
      setCanResendOtp(false);
      showInfo('OTP Resent', 'New verification code sent');
    } else {
      showError('Failed to Resend OTP', result.message);
    }
  };

  // Phone number formatting
  const formatPhoneNumber = (phone) => {
    // Remove all non-digits
    const cleaned = phone.replace(/\D/g, '');

    // Add +91 if not present
    if (cleaned.length === 10) {
      return `+91${cleaned}`;
    } else if (cleaned.length === 12 && cleaned.startsWith('91')) {
      return `+${cleaned}`;
    } else if (cleaned.length === 13 && cleaned.startsWith('91')) {
      return `+${cleaned}`;
    }

    return cleaned.startsWith('+') ? phone : `+${cleaned}`;
  };

  // Phone number validation
  const isValidPhoneNumber = (phone) => {
    const phoneRegex = /^\+91[6-9]\d{9}$/;
    return phoneRegex.test(phone);
  };

  // Go back to phone step
  const goBackToPhone = () => {
    setStep(1);
    setOtpTimer(0);
    setCanResendOtp(false);
    otpForm.reset();
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-50 flex items-center justify-center p-4">
      <div className="max-w-md w-full">
        {/* Logo and Header */}
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-blue-600 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg">
            <Droplets className="h-8 w-8 text-white" />
          </div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Welcome to Waterwala</h1>
          <p className="text-gray-600">
            {step === 1 ? 'Enter your phone number to get started' : 'Enter the verification code sent to your phone'}
          </p>
        </div>

        {/* Login Form Card */}
        <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
          {/* Step Indicator */}
          <div className="flex items-center justify-center mb-8">
            <div className="flex items-center space-x-3">
              <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium ${
                step >= 1 ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-600'
              }`}>
                <Phone className="h-4 w-4" />
              </div>
              <div className={`w-12 h-0.5 ${step >= 2 ? 'bg-blue-600' : 'bg-gray-200'}`} />
              <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium ${
                step >= 2 ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-600'
              }`}>
                <Key className="h-4 w-4" />
              </div>
            </div>
          </div>

          {/* Step 1: Phone Number */}
          {step === 1 && (
            <form onSubmit={phoneForm.handleSubmit(handlePhoneSubmit)} className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Phone Number
                </label>
                <div className="relative">
                  <Phone className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                  <input
                    {...phoneForm.register('phone', {
                      required: 'Phone number is required',
                      pattern: {
                        value: /^[6-9]\d{9}$/,
                        message: 'Please enter a valid 10-digit mobile number'
                      }
                    })}
                    type="tel"
                    placeholder="Enter your mobile number"
                    className="input-field pl-11"
                    autoComplete="tel"
                  />
                </div>
                {phoneForm.formState.errors.phone && (
                  <p className="text-red-600 text-sm mt-1">
                    {phoneForm.formState.errors.phone.message}
                  </p>
                )}
                <p className="text-gray-500 text-xs mt-1">
                  We'll send you a verification code via SMS
                </p>
              </div>

              <button
                type="submit"
                disabled={loading || !phoneForm.formState.isValid}
                className="btn-primary w-full flex items-center justify-center space-x-2 py-3"
              >
                {loading ? (
                  <div className="spinner" />
                ) : (
                  <>
                    <span>Send OTP</span>
                    <ArrowRight className="h-4 w-4" />
                  </>
                )}
              </button>
            </form>
          )}

          {/* Step 2: OTP Verification */}
          {step === 2 && (
            <div className="space-y-6">
              {/* Back Button */}
              <button
                onClick={goBackToPhone}
                className="flex items-center space-x-2 text-gray-600 hover:text-gray-800 mb-4"
              >
                <ArrowLeft className="h-4 w-4" />
                <span className="text-sm">Change phone number</span>
              </button>

              {/* Phone Display */}
              <div className="text-center mb-6">
                <p className="text-sm text-gray-600">Verification code sent to</p>
                <p className="font-semibold text-gray-900">{phoneNumber}</p>
              </div>

              <form onSubmit={otpForm.handleSubmit(handleOtpSubmit)} className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Enter OTP
                  </label>
                  <div className="relative">
                    <Key className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                      {...otpForm.register('otp', {
                        required: 'OTP is required',
                        pattern: {
                          value: /^\d{6}$/,
                          message: 'Please enter a valid 6-digit OTP'
                        }
                      })}
                      type="text"
                      inputMode="numeric"
                      maxLength={6}
                      placeholder="Enter 6-digit OTP"
                      className="input-field pl-11 text-center text-lg tracking-widest font-mono"
                      autoComplete="one-time-code"
                    />
                  </div>
                  {otpForm.formState.errors.otp && (
                    <p className="text-red-600 text-sm mt-1">
                      {otpForm.formState.errors.otp.message}
                    </p>
                  )}
                </div>

                {/* Timer and Resend */}
                <div className="text-center">
                  {otpTimer > 0 ? (
                    <div className="flex items-center justify-center space-x-2 text-gray-600">
                      <Clock className="h-4 w-4" />
                      <span className="text-sm">Resend OTP in {formatTimer(otpTimer)}</span>
                    </div>
                  ) : (
                    <button
                      type="button"
                      onClick={handleResendOtp}
                      disabled={loading}
                      className="text-blue-600 hover:text-blue-700 text-sm font-medium disabled:opacity-50"
                    >
                      Resend OTP
                    </button>
                  )}
                </div>

                <button
                  type="submit"
                  disabled={loading || !otpForm.formState.isValid}
                  className="btn-primary w-full flex items-center justify-center space-x-2 py-3"
                >
                  {loading ? (
                    <div className="spinner" />
                  ) : (
                    <>
                      <span>Verify & Login</span>
                      <ArrowRight className="h-4 w-4" />
                    </>
                  )}
                </button>
              </form>
            </div>
          )}

          {/* Register Link */}
          <div className="mt-8 text-center">
            <p className="text-gray-600 text-sm">
              Don't have an account?{' '}
              <Link to="/register" className="text-blue-600 hover:text-blue-700 font-medium">
                Sign up
              </Link>
            </p>
          </div>
        </div>

        {/* Help Section */}
        <div className="mt-8 text-center">
          <div className="bg-white rounded-xl border border-gray-100 p-4">
            <h3 className="font-medium text-gray-900 mb-2">Need Help?</h3>
            <p className="text-sm text-gray-600 mb-3">
              Having trouble logging in? Contact our support team
            </p>
            <div className="flex items-center justify-center space-x-4 text-sm">
              <a href="tel:+919876543210" className="flex items-center space-x-1 text-blue-600 hover:text-blue-700">
                <Phone className="h-4 w-4" />
                <span>+91 98765 43210</span>
              </a>
              <span className="text-gray-300">|</span>
              <a href="mailto:support@waterwala.com" className="text-blue-600 hover:text-blue-700">
                support@waterwala.com
              </a>
            </div>
          </div>
        </div>

        {/* Features Section */}
        <div className="mt-8 grid grid-cols-3 gap-4 text-center">
          <div className="bg-white rounded-lg p-4 border border-gray-100">
            <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-2">
              <Droplets className="h-4 w-4 text-blue-600" />
            </div>
            <p className="text-xs text-gray-600">Pure Water</p>
          </div>
          <div className="bg-white rounded-lg p-4 border border-gray-100">
            <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-2">
              <Clock className="h-4 w-4 text-green-600" />
            </div>
            <p className="text-xs text-gray-600">Quick Delivery</p>
          </div>
          <div className="bg-white rounded-lg p-4 border border-gray-100">
            <div className="w-8 h-8 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-2">
              <Phone className="h-4 w-4 text-purple-600" />
            </div>
            <p className="text-xs text-gray-600">24/7 Support</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;