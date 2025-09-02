import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { Droplets, Key, ArrowRight, ArrowLeft, Clock, RefreshCw } from 'lucide-react';
import { useNotification } from '../../context/NotificationContext';
import { apiClient, API_BASE_URLS } from '../../services/api';

const OTPVerification = () => {
  const [otpTimer, setOtpTimer] = useState(0);
  const [canResendOtp, setCanResendOtp] = useState(false);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const location = useLocation();
  const { showSuccess, showError, showInfo } = useNotification();

  // Get data from navigation state (phone, email, type)
  const { contact, type = 'PHONE_VERIFICATION', redirectTo = '/login' } = location.state || {};

  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
    reset
  } = useForm({
    defaultValues: {
      otp: ''
    }
  });

  // Initialize timer on mount
  useEffect(() => {
    if (!contact) {
      navigate('/login');
      return;
    }

    // Start timer for 10 minutes (600 seconds)
    setOtpTimer(600);
    setCanResendOtp(false);
  }, [contact, navigate]);

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

  // Handle OTP verification
  const handleOtpSubmit = async (data) => {
    if (data.otp.length !== 6) {
      showError('Invalid OTP', 'Please enter a 6-digit OTP');
      return;
    }

    setLoading(true);
    try {
      const response = await apiClient.post(`${API_BASE_URLS.USER_SERVICE}/otp/verify`, null, {
        params: {
          contact: contact,
          otpCode: data.otp,
          type: type
        }
      });

      if (response.data.success) {
        showSuccess('Verification Successful', 'Your contact has been verified successfully');

        // Redirect based on type
        setTimeout(() => {
          navigate(redirectTo, {
            state: {
              verified: true,
              contact: contact,
              type: type
            }
          });
        }, 1500);
      } else {
        showError('Verification Failed', response.data.message || 'Invalid or expired OTP');
      }
    } catch (error) {
      console.error('OTP verification error:', error);
      showError('Verification Error', error.response?.data?.message || 'Failed to verify OTP');
    } finally {
      setLoading(false);
    }
  };

  // Resend OTP
  const handleResendOtp = async () => {
    setLoading(true);
    try {
      let endpoint;
      let params = {};

      // Determine which endpoint to call based on type
      switch (type) {
        case 'EMAIL_VERIFICATION':
          endpoint = `${API_BASE_URLS.USER_SERVICE}/otp/send/email-verification`;
          params.email = contact;
          break;
        case 'PHONE_VERIFICATION':
          endpoint = `${API_BASE_URLS.USER_SERVICE}/otp/send/phone-verification`;
          params.phone = contact;
          break;
        case 'PASSWORD_RESET':
          endpoint = `${API_BASE_URLS.USER_SERVICE}/otp/send/password-reset`;
          params.phone = contact;
          break;
        default:
          // Default to login OTP
          endpoint = `${API_BASE_URLS.USER_SERVICE}/auth/send-otp`;
          params.phone = contact;
      }

      const response = await apiClient.post(endpoint, null, { params });

      if (response.data.success) {
        setOtpTimer(600); // Reset timer to 10 minutes
        setCanResendOtp(false);
        reset(); // Clear OTP input
        showInfo('OTP Resent', 'New verification code sent');
      } else {
        showError('Failed to Resend OTP', response.data.message);
      }
    } catch (error) {
      console.error('Resend OTP error:', error);
      showError('Resend Failed', error.response?.data?.message || 'Failed to resend OTP');
    } finally {
      setLoading(false);
    }
  };

  // Go back
  const goBack = () => {
    navigate(-1);
  };

  // Get verification type display text
  const getVerificationTypeText = () => {
    switch (type) {
      case 'EMAIL_VERIFICATION':
        return { title: 'Email Verification', subtitle: 'Verify your email address' };
      case 'PHONE_VERIFICATION':
        return { title: 'Phone Verification', subtitle: 'Verify your phone number' };
      case 'PASSWORD_RESET':
        return { title: 'Password Reset', subtitle: 'Reset your password' };
      default:
        return { title: 'OTP Verification', subtitle: 'Enter verification code' };
    }
  };

  const verificationText = getVerificationTypeText();

  // Redirect if no contact provided
  if (!contact) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-50 flex items-center justify-center p-4">
        <div className="text-center">
          <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <Key className="h-8 w-8 text-red-600" />
          </div>
          <h2 className="text-xl font-bold text-gray-900 mb-4">Invalid Access</h2>
          <p className="text-gray-600 mb-6">No contact information provided for verification.</p>
          <Link to="/login" className="btn-primary">
            Go to Login
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-50 flex items-center justify-center p-4">
      <div className="max-w-md w-full">
        {/* Logo and Header */}
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-blue-600 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg">
            <Droplets className="h-8 w-8 text-white" />
          </div>
          <h1 className="text-2xl font-bold text-gray-900 mb-2">{verificationText.title}</h1>
          <p className="text-gray-600">{verificationText.subtitle}</p>
        </div>

        {/* OTP Form Card */}
        <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
          {/* Back Button */}
          <button
            onClick={goBack}
            className="flex items-center space-x-2 text-gray-600 hover:text-gray-800 mb-6 transition-colors"
          >
            <ArrowLeft className="h-4 w-4" />
            <span className="text-sm">Back</span>
          </button>

          {/* Contact Display */}
          <div className="text-center mb-6">
            <p className="text-sm text-gray-600">Verification code sent to</p>
            <p className="font-semibold text-gray-900">{contact}</p>
          </div>

          {/* OTP Form */}
          <form onSubmit={handleSubmit(handleOtpSubmit)} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Enter Verification Code
              </label>
              <div className="relative">
                <Key className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                <input
                  {...register('otp', {
                    required: 'OTP is required',
                    pattern: {
                      value: /^\d{6}$/,
                      message: 'Please enter a valid 6-digit OTP'
                    }
                  })}
                  type="text"
                  inputMode="numeric"
                  maxLength={6}
                  placeholder="Enter 6-digit code"
                  className="input-field pl-11 text-center text-lg tracking-widest font-mono"
                  autoComplete="one-time-code"
                />
              </div>
              {errors.otp && (
                <p className="text-red-600 text-sm mt-1">{errors.otp.message}</p>
              )}
            </div>

            {/* Timer and Resend */}
            <div className="text-center">
              {otpTimer > 0 ? (
                <div className="flex items-center justify-center space-x-2 text-gray-600">
                  <Clock className="h-4 w-4" />
                  <span className="text-sm">Code expires in {formatTimer(otpTimer)}</span>
                </div>
              ) : (
                <button
                  type="button"
                  onClick={handleResendOtp}
                  disabled={loading}
                  className="flex items-center space-x-2 text-blue-600 hover:text-blue-700 text-sm font-medium disabled:opacity-50 mx-auto transition-colors"
                >
                  <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
                  <span>Resend Code</span>
                </button>
              )}
            </div>

            <button
              type="submit"
              disabled={loading || !isValid}
              className="btn-primary w-full flex items-center justify-center space-x-2 py-3"
            >
              {loading ? (
                <div className="spinner" />
              ) : (
                <>
                  <span>Verify Code</span>
                  <ArrowRight className="h-4 w-4" />
                </>
              )}
            </button>
          </form>

          {/* Help Text */}
          <div className="mt-6 text-center">
            <p className="text-gray-500 text-xs">
              Didn't receive the code? Check your spam folder or try resending
            </p>
          </div>
        </div>

        {/* Support Section */}
        <div className="mt-6 text-center">
          <div className="bg-white rounded-xl border border-gray-100 p-4">
            <p className="text-sm text-gray-600 mb-2">Need help with verification?</p>
            <a
              href="tel:+919876543210"
              className="text-blue-600 hover:text-blue-700 text-sm font-medium"
            >
              Contact Support: +91 98765 43210
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OTPVerification;