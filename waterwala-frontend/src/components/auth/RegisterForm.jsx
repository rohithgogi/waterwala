import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { User, Phone, Mail } from 'lucide-react';
import { Input } from '../common/Input';
import { Button } from '../common/Button';
import { userService } from '../../services/userService';
import { validators } from '../../utils';
import toast from 'react-hot-toast';

export const RegisterForm = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const { register, handleSubmit, formState: { errors }, watch } = useForm();

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      const registrationData = {
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        phone: data.phone,
        role: 'CUSTOMER'
      };

      const response = await userService.register(registrationData);
      toast.success('Registration successful! Please login.');
      navigate('/login');
    } catch (error) {
      const message = error.response?.data?.message || 'Registration failed';
      toast.error(message);
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
            Create Account
          </h2>
          <p className="text-gray-600">
            Join WaterWala for convenient water delivery
          </p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="First Name"
              icon={User}
              placeholder="First name"
              {...register('firstName', {
                required: 'First name is required',
                validate: (value) => validators.name(value, 'First name')
              })}
              error={errors.firstName?.message}
            />

            <Input
              label="Last Name"
              icon={User}
              placeholder="Last name"
              {...register('lastName', {
                required: 'Last name is required',
                validate: (value) => validators.name(value, 'Last name')
              })}
              error={errors.lastName?.message}
            />
          </div>

          <Input
            label="Email Address"
            icon={Mail}
            type="email"
            placeholder="your.email@example.com"
            {...register('email', {
              required: 'Email is required',
              validate: (value) => validators.email(value)
            })}
            error={errors.email?.message}
          />

          <Input
            label="Phone Number"
            icon={Phone}
            type="tel"
            placeholder="10-digit phone number"
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
            Register
          </Button>

          <div className="text-center">
            <p className="text-sm text-gray-600">
              Already have an account?{' '}
              <button
                type="button"
                onClick={() => navigate('/login')}
                className="text-blue-600 hover:text-blue-700 font-semibold"
              >
                Login
              </button>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};