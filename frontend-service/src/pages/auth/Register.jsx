import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import {
  Droplets,
  User,
  Phone,
  Mail,
  MapPin,
  ArrowRight,
  Eye,
  EyeOff,
  CheckCircle,
  AlertCircle
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useNotification } from '../../context/NotificationContext';

const Register = () => {
  const [step, setStep] = useState(1); // 1: Basic Info, 2: Contact Verification, 3: Success
  const [showPassword, setShowPassword] = useState(false);
  const [emailVerified, setEmailVerified] = useState(false);
  const [phoneVerified, setPhoneVerified] = useState(false);

  const navigate = useNavigate();
  const { register: registerUser, loading } = useAuth();
  const { showSuccess, showError, showInfo } = useNotification();

  // Form management
  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
    watch,
    getValues,
    setValue
  } = useForm({
    mode: 'onChange',
    defaultValues: {
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      password: '',
      confirmPassword: '',
      address: '',
      pincode: '',
      city: '',
      state: '',
      role: 'CUSTOMER'
    }
  });

  // Watch password for confirmation validation
  const watchPassword = watch('password');

  // Handle form submission
  const handleRegistration = async (data) => {
    try {
      // Format phone number
      const formattedPhone = formatPhoneNumber(data.phone);

      const registrationData = {
        firstName: data.firstName.trim(),
        lastName: data.lastName.trim(),
        email: data.email.toLowerCase().trim(),
        phone: formattedPhone,
        password: data.password,
        address: data.address.trim(),
        pincode: data.pincode.trim(),
        city: data.city.trim(),
        state: data.state.trim(),
        role: data.role
      };

      const result = await registerUser(registrationData);

      if (result.success) {
        setStep(3);
        showSuccess('Registration Successful', 'Account created successfully!');
      } else {
        showError('Registration Failed', result.message);
      }
    } catch (error) {
      showError('Registration Error', 'An unexpected error occurred');
    }
  };

  // Phone number formatting
  const formatPhoneNumber = (phone) => {
    const cleaned = phone.replace(/\D/g, '');
    if (cleaned.length === 10) {
      return `+91${cleaned}`;
    }
    return cleaned.startsWith('+') ? phone : `+${cleaned}`;
  };

  // Navigate to login
  const goToLogin = () => {
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-50 py-8 px-4">
      <div className="max-w-2xl mx-auto">
        {/* Logo and Header */}
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-blue-600 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg">
            <Droplets className="h-8 w-8 text-white" />
          </div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Join Waterwala</h1>
          <p className="text-gray-600">Create your account to start ordering pure water</p>
        </div>

        {step === 3 ? (
          /* Success Step */
          <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8 text-center">
            <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <CheckCircle className="h-8 w-8 text-green-600" />
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-4">Account Created Successfully!</h2>
            <p className="text-gray-600 mb-8">
              Your Waterwala account has been created. You can now login with your phone number and start ordering.
            </p>
            <button
              onClick={goToLogin}
              className="btn-primary flex items-center space-x-2 mx-auto"
            >
              <span>Continue to Login</span>
              <ArrowRight className="h-4 w-4" />
            </button>
          </div>
        ) : (
          /* Registration Form */
          <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
            <form onSubmit={handleSubmit(handleRegistration)} className="space-y-6">
              {/* Personal Information */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    First Name *
                  </label>
                  <div className="relative">
                    <User className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                      {...register('firstName', {
                        required: 'First name is required',
                        minLength: { value: 2, message: 'Minimum 2 characters required' }
                      })}
                      type="text"
                      placeholder="First Name"
                      className={`input-field pl-11 ${errors.firstName ? 'input-error' : ''}`}
                    />
                  </div>
                  {errors.firstName && (
                    <p className="text-red-600 text-sm mt-1">{errors.firstName.message}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Last Name *
                  </label>
                  <input
                    {...register('lastName', {
                      required: 'Last name is required',
                      minLength: { value: 2, message: 'Minimum 2 characters required' }
                    })}
                    type="text"
                    placeholder="Last Name"
                    className={`input-field ${errors.lastName ? 'input-error' : ''}`}
                  />
                  {errors.lastName && (
                    <p className="text-red-600 text-sm mt-1">{errors.lastName.message}</p>
                  )}
                </div>
              </div>

              {/* Contact Information */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Email Address *
                  </label>
                  <div className="relative">
                    <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                      {...register('email', {
                        required: 'Email is required',
                        pattern: {
                          value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                          message: 'Please enter a valid email address'
                        }
                      })}
                      type="email"
                      placeholder="Email Address"
                      className={`input-field pl-11 ${errors.email ? 'input-error' : ''}`}
                    />
                  </div>
                  {errors.email && (
                    <p className="text-red-600 text-sm mt-1">{errors.email.message}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Phone Number *
                  </label>
                  <div className="relative">
                    <Phone className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                      {...register('phone', {
                        required: 'Phone number is required',
                        pattern: {
                          value: /^[6-9]\d{9}$/,
                          message: 'Please enter a valid 10-digit mobile number'
                        }
                      })}
                      type="tel"
                      placeholder="Mobile Number"
                      className={`input-field pl-11 ${errors.phone ? 'input-error' : ''}`}
                    />
                  </div>
                  {errors.phone && (
                    <p className="text-red-600 text-sm mt-1">{errors.phone.message}</p>
                  )}
                </div>
              </div>

              {/* Password */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Password *
                  </label>
                  <div className="relative">
                    <input
                      {...register('password', {
                        required: 'Password is required',
                        minLength: { value: 8, message: 'Password must be at least 8 characters' },
                        pattern: {
                          value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
                          message: 'Password must contain uppercase, lowercase and number'
                        }
                      })}
                      type={showPassword ? 'text' : 'password'}
                      placeholder="Create Password"
                      className={`input-field pr-11 ${errors.password ? 'input-error' : ''}`}
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                    >
                      {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
                    </button>
                  </div>
                  {errors.password && (
                    <p className="text-red-600 text-sm mt-1">{errors.password.message}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Confirm Password *
                  </label>
                  <input
                    {...register('confirmPassword', {
                      required: 'Please confirm your password',
                      validate: value => value === watchPassword || 'Passwords do not match'
                    })}
                    type="password"
                    placeholder="Confirm Password"
                    className={`input-field ${errors.confirmPassword ? 'input-error' : ''}`}
                  />
                  {errors.confirmPassword && (
                    <p className="text-red-600 text-sm mt-1">{errors.confirmPassword.message}</p>
                  )}
                </div>
              </div>

              {/* Address Information */}
              <div className="space-y-4">
                <h3 className="text-lg font-semibold text-gray-900 flex items-center space-x-2">
                  <MapPin className="h-5 w-5" />
                  <span>Address Information</span>
                </h3>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Full Address *
                  </label>
                  <textarea
                    {...register('address', {
                      required: 'Address is required',
                      minLength: { value: 10, message: 'Please provide a complete address' }
                    })}
                    placeholder="House/Flat No, Building Name, Street, Locality"
                    rows={3}
                    className={`input-field resize-none ${errors.address ? 'input-error' : ''}`}
                  />
                  {errors.address && (
                    <p className="text-red-600 text-sm mt-1">{errors.address.message}</p>
                  )}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Pincode *
                    </label>
                    <input
                      {...register('pincode', {
                        required: 'Pincode is required',
                        pattern: {
                          value: /^\d{6}$/,
                          message: 'Please enter a valid 6-digit pincode'
                        }
                      })}
                      type="text"
                      placeholder="Pincode"
                      maxLength={6}
                      className={`input-field ${errors.pincode ? 'input-error' : ''}`}
                    />
                    {errors.pincode && (
                      <p className="text-red-600 text-sm mt-1">{errors.pincode.message}</p>
                    )}
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      City *
                    </label>
                    <input
                      {...register('city', {
                        required: 'City is required',
                        minLength: { value: 2, message: 'Invalid city name' }
                      })}
                      type="text"
                      placeholder="City"
                      className={`input-field ${errors.city ? 'input-error' : ''}`}
                    />
                    {errors.city && (
                      <p className="text-red-600 text-sm mt-1">{errors.city.message}</p>
                    )}
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      State *
                    </label>
                    <input
                      {...register('state', {
                        required: 'State is required',
                        minLength: { value: 2, message: 'Invalid state name' }
                      })}
                      type="text"
                      placeholder="State"
                      className={`input-field ${errors.state ? 'input-error' : ''}`}
                    />
                    {errors.state && (
                      <p className="text-red-600 text-sm mt-1">{errors.state.message}</p>
                    )}
                  </div>
                </div>
              </div>

              {/* Account Type */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Account Type
                </label>
                <select
                  {...register('role')}
                  className="input-field"
                >
                  <option value="CUSTOMER">Customer Account</option>
                  <option value="BUSINESS_OWNER">Business Account</option>
                </select>
                <p className="text-gray-500 text-xs mt-1">
                  Choose Business Account if you want to sell water products
                </p>
              </div>

              {/* Terms and Conditions */}
              <div className="flex items-start space-x-3">
                <input
                  {...register('acceptTerms', {
                    required: 'You must accept the terms and conditions'
                  })}
                  type="checkbox"
                  className="mt-1 h-4 w-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                />
                <div className="text-sm">
                  <label className="text-gray-700">
                    I agree to the{' '}
                    <a href="#" className="text-blue-600 hover:text-blue-700 font-medium">
                      Terms of Service
                    </a>{' '}
                    and{' '}
                    <a href="#" className="text-blue-600 hover:text-blue-700 font-medium">
                      Privacy Policy
                    </a>
                  </label>
                  {errors.acceptTerms && (
                    <p className="text-red-600 text-xs mt-1">{errors.acceptTerms.message}</p>
                  )}
                </div>
              </div>

              {/* Submit Button */}
              <button
                type="submit"
                disabled={loading || !isValid}
                className="btn-primary w-full flex items-center justify-center space-x-2 py-3"
              >
                {loading ? (
                  <div className="spinner" />
                ) : (
                  <>
                    <span>Create Account</span>
                    <ArrowRight className="h-4 w-4" />
                  </>
                )}
              </button>
            </form>

            {/* Login Link */}
            <div className="mt-6 text-center">
              <p className="text-gray-600 text-sm">
                Already have an account?{' '}
                <Link to="/login" className="text-blue-600 hover:text-blue-700 font-medium">
                  Sign in
                </Link>
              </p>
            </div>
          </div>
        )}

        {/* Benefits Section */}
        <div className="mt-8 grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-white rounded-xl border border-gray-100 p-6 text-center">
            <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <Droplets className="h-6 w-6 text-blue-600" />
            </div>
            <h3 className="font-semibold text-gray-900 mb-2">Pure & Safe Water</h3>
            <p className="text-gray-600 text-sm">
              Premium quality water from trusted brands delivered to your doorstep
            </p>
          </div>

          <div className="bg-white rounded-xl border border-gray-100 p-6 text-center">
            <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <ArrowRight className="h-6 w-6 text-green-600" />
            </div>
            <h3 className="font-semibold text-gray-900 mb-2">Fast Delivery</h3>
            <p className="text-gray-600 text-sm">
              Quick and reliable delivery service across your city
            </p>
          </div>

          <div className="bg-white rounded-xl border border-gray-100 p-6 text-center">
            <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <Phone className="h-6 w-6 text-purple-600" />
            </div>
            <h3 className="font-semibold text-gray-900 mb-2">24/7 Support</h3>
            <p className="text-gray-600 text-sm">
              Round-the-clock customer support for all your queries
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;