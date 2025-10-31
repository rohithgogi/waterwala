import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Lock } from 'lucide-react';
import { Input } from '../common/Input';
import { Button } from '../common/Button';
import { Alert } from '../common/Alert';
import { otpService } from '../../services/otpService';
import { validators } from '../../utils';
import toast from 'react-hot-toast';

export const OTPVerification = ({
  contact,
  type,
  onVerified,
  onResend
}) => {
  const [loading, setLoading] = useState(false);
  const [countdown, setCountdown] = useState(60);
  const [canResend, setCanResend] = useState(false);
  const { register, handleSubmit, formState: { errors } } = useForm();

  useEffect(() => {
    if (countdown > 0) {
      const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
      return () => clearTimeout(timer);
    } else {
      setCanResend(true);
    }
  }, [countdown]);

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      const response = await otpService.verifyOTP(contact, data.otp, type);
      if (response.data.success) {
        toast.success('OTP verified successfully!');
        onVerified?.();
      }
    } catch (error) {
      toast.error('Invalid or expired OTP');
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    setLoading(true);
    setCanResend(false);
    setCountdown(60);

    try {
      await onResend?.();
      toast.success('OTP resent successfully!');
    } catch (error) {
      toast.error('Failed to resend OTP');
      setCanResend(true);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <Alert
        type="info"
        message={`Enter the 6-digit OTP sent to ${contact}`}
      />

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          label="Enter OTP"
          icon={Lock}
          type="text"
          placeholder="6-digit OTP"
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
          Verify OTP
        </Button>

        <div className="text-center">
          {!canResend ? (
            <p className="text-sm text-gray-600">
              Resend OTP in <strong>{countdown}s</strong>
            </p>
          ) : (
            <button
              type="button"
              onClick={handleResend}
              disabled={loading}
              className="text-sm text-blue-600 hover:text-blue-700 font-semibold"
            >
              Resend OTP
            </button>
          )}
        </div>
      </form>
    </div>
  );
};