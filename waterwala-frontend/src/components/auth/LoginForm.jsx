import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { Phone, Lock } from 'lucide-react';
import { Input } from '../common/Input';
import { Button } from '../common/Button';
import { authService } from '../../services/authService';
import { validators, helpers } from '../../utils';
import { DEVICE_TYPES } from '../../utils/constants';
import toast from 'react-hot-toast';
import { useAuth } from '../../context/AuthContext';

export const LoginForm = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [step, setStep] = useState('phone'); // 'phone' or 'otp'
  const [phoneNumber, setPhoneNumber] = useState('');
  const [loading, setLoading] = useState(false);
  const [otpSent, setOtpSent] = useState(false);

  const { register, handleSubmit, formState: { errors }, watch, setValue } = useForm();

  const handleSendOTP = async (data) => {
    const phoneError = validators.phone(data.phone);
    if (phoneError) {
      toast.error(phoneError);
      return;
    }

    setLoading(true);
    try {
      await authService.sendLoginOTP(data.phone);
      setPhoneNumber(data.phone);
      setStep('otp');
      setOtpSent(true);
      toast.success('OTP sent successfully!');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to send OTP');
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = async (data) => {
    const otpError = validators.otp(data.otp);
    if (otpError) {
      toast.error(otpError);
      return;
    }

    setLoading(true);
    try {
      const deviceId = helpers.generateDeviceId();
      const loginData = {
        phone: phoneNumber,
        otp: data.otp,
        deviceId: deviceId,
        deviceType: DEVICE_TYPES.WEB
      };

      await login(loginData);
      toast.success('Login successful!');
      navigate('/dashboard');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  const handleResendOTP = async () => {
    setLoading(true);
    try {
      await authService.sendLoginOTP(phoneNumber);
      toast.success('OTP resent successfully!');
    } catch (error) {
      toast.error('Failed to resend OTP');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full max-w-md mx-auto">
      <div className="bg-white rounded-2xl shadow-xl p-8">
        {/* Header */}
        <div className="text-center mb-8">
          <h2 className="text-3xl font-bold text-gray-900 mb-2">
            Welcome Back!
          </h2>
          <p className="text-gray-600">
            {step === 'phone' ? 'Enter your phone number to login' : 'Enter the OTP sent to your phone'}
          </p>
        </div>

        {/* Phone Number Step */}
        {step === 'phone' && (
          <form onSubmit={handleSubmit(handleSendOTP)} className="space-y-6">
            <Input
              label="Phone Number"
              icon={Phone}
              type="tel"
              placeholder="Enter 10-digit phone number"
              {...register('phone', {
                required: 'Phone number is required',
                validate: (value) => validators.phone(value)
              })}
              error={errors.phone?.message}
              maxLength={10}
            />

            <Button
              type="submit"
              className="w-full"
              loading={loading}
            >
              Send OTP
            </Button>

            <div className="text-center">
              <p className="text-sm text-gray-600">
                Don't have an account?{' '}
                <button
                  type="button"
                  onClick={() => navigate('/register')}
                  className="text-blue-600 hover:text-blue-700 font-semibold"
                >
                  Register
                </button>
              </p>
            </div>
          </form>
        )}

        {/* OTP Step */}
        {step === 'otp' && (
          <form onSubmit={handleSubmit(handleLogin)} className="space-y-6">
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-4">
              <p className="text-sm text-blue-800">
                OTP sent to <strong>{helpers.formatPhone(phoneNumber)}</strong>
              </p>
            </div>

            <Input
              label="Enter OTP"
              icon={Lock}
              type="text"
              placeholder="Enter 6-digit OTP"
              {...register('otp', {
                required: 'OTP is required',
                validate: (value) => validators.otp(value)
              })}
              error={errors.otp?.message}
              maxLength={6}
              autoFocus
            />

            <Button
              type="submit"
              className="w-full"
              loading={loading}
            >
              Login
            </Button>

            <div className="text-center space-y-2">
              <button
                type="button"
                onClick={handleResendOTP}
                disabled={loading}
                className="text-sm text-blue-600 hover:text-blue-700 font-semibold"
              >
                Resend OTP
              </button>

              <p className="text-sm text-gray-600">
                <button
                  type="button"
                  onClick={() => {
                    setStep('phone');
                    setValue('otp', '');
                  }}
                  className="text-blue-600 hover:text-blue-700 font-semibold"
                >
                  Change Phone Number
                </button>
              </p>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};
