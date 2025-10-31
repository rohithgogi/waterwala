import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { User, Mail, Phone, Edit2, CheckCircle, XCircle } from 'lucide-react';
import { Card } from '../components/common/Card';
import { Input } from '../components/common/Input';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { OTPVerification } from '../components/auth/OTPVerification';
import { useAuth } from '../context/AuthContext';
import { useUser } from '../hooks/useUser';
import { otpService } from '../services/otpService';
import { validators, helpers } from '../utils';
import { OTP_TYPES } from '../utils/constants';
import toast from 'react-hot-toast';

export default function Profile() {
  const { user: authUser, updateUser: updateAuthUser } = useAuth();
  const { user, loading, updateUser, verifyEmail, verifyPhone } = useUser(authUser?.id);
  const [editing, setEditing] = useState(false);
  const [verifyingEmail, setVerifyingEmail] = useState(false);
  const [verifyingPhone, setVerifyingPhone] = useState(false);
  const { register, handleSubmit, formState: { errors }, reset } = useForm();

  const onSubmit = async (data) => {
    try {
      const updated = await updateUser({
        firstName: data.firstName,
        lastName: data.lastName,
        profileImageURL: data.profileImageURL
      });
      updateAuthUser(updated);
      setEditing(false);
      reset();
    } catch (error) {
      // Error handled in hook
    }
  };

  const handleSendEmailOTP = async () => {
    try {
      await otpService.sendEmailVerificationOTP(user.email);
      setVerifyingEmail(true);
    } catch (error) {
      toast.error('Failed to send verification email');
    }
  };

  const handleSendPhoneOTP = async () => {
    try {
      await otpService.sendPhoneVerificationOTP(user.phone);
      setVerifyingPhone(true);
    } catch (error) {
      toast.error('Failed to send verification SMS');
    }
  };

  const handleEmailVerified = async () => {
    await verifyEmail();
    setVerifyingEmail(false);
    updateAuthUser({ ...authUser, emailVerified: true });
  };

  const handlePhoneVerified = async () => {
    await verifyPhone();
    setVerifyingPhone(false);
    updateAuthUser({ ...authUser, phoneVerified: true });
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="w-12 h-12 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold text-gray-900">My Profile</h1>
        {!editing && (
          <Button
            onClick={() => setEditing(true)}
            variant="outline"
            icon={Edit2}
          >
            Edit Profile
          </Button>
        )}
      </div>

      <div className="grid md:grid-cols-3 gap-6">
        {/* Profile Card */}
        <Card className="md:col-span-1">
          <div className="text-center">
            <div className="w-24 h-24 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-full flex items-center justify-center text-white text-3xl font-bold mx-auto mb-4">
              {helpers.getUserInitials(user?.firstName, user?.lastName)}
            </div>
            <h2 className="text-xl font-bold text-gray-900 mb-1">
              {user?.firstName} {user?.lastName}
            </h2>
            <p className="text-gray-600 mb-4">{user?.role}</p>
            <Badge variant={user?.status === 'ACTIVE' ? 'success' : 'warning'}>
              {user?.status}
            </Badge>
          </div>
        </Card>

        {/* Profile Details */}
        <Card className="md:col-span-2" title="Profile Information">
          {editing ? (
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <Input
                  label="First Name"
                  defaultValue={user?.firstName}
                  {...register('firstName', {
                    required: 'First name is required',
                    validate: (value) => validators.name(value, 'First name')
                  })}
                  error={errors.firstName?.message}
                />

                <Input
                  label="Last Name"
                  defaultValue={user?.lastName}
                  {...register('lastName', {
                    required: 'Last name is required',
                    validate: (value) => validators.name(value, 'Last name')
                  })}
                  error={errors.lastName?.message}
                />
              </div>

              <div className="flex gap-3">
                <Button type="submit">Save Changes</Button>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => {
                    setEditing(false);
                    reset();
                  }}
                >
                  Cancel
                </Button>
              </div>
            </form>
          ) : (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm text-gray-600">First Name</label>
                  <p className="font-medium text-gray-900">{user?.firstName}</p>
                </div>
                <div>
                  <label className="text-sm text-gray-600">Last Name</label>
                  <p className="font-medium text-gray-900">{user?.lastName}</p>
                </div>
              </div>

              <div className="flex items-center justify-between py-3 border-t border-gray-100">
                <div className="flex items-center gap-3">
                  <Mail className="w-5 h-5 text-gray-400" />
                  <div>
                    <p className="text-sm text-gray-600">Email</p>
                    <p className="font-medium text-gray-900">{user?.email}</p>
                  </div>
                </div>
                {user?.emailVerified ? (
                  <Badge variant="success">
                    <CheckCircle className="w-3 h-3 mr-1" />
                    Verified
                  </Badge>
                ) : (
                  <Button
                    onClick={handleSendEmailOTP}
                    size="sm"
                    variant="outline"
                  >
                    Verify Email
                  </Button>
                )}
              </div>

              <div className="flex items-center justify-between py-3 border-t border-gray-100">
                <div className="flex items-center gap-3">
                  <Phone className="w-5 h-5 text-gray-400" />
                  <div>
                    <p className="text-sm text-gray-600">Phone</p>
                    <p className="font-medium text-gray-900">
                      {helpers.formatPhone(user?.phone)}
                    </p>
                  </div>
                </div>
                {user?.phoneVerified ? (
                  <Badge variant="success">
                    <CheckCircle className="w-3 h-3 mr-1" />
                    Verified
                  </Badge>
                ) : (
                  <Button
                    onClick={handleSendPhoneOTP}
                    size="sm"
                    variant="outline"
                  >
                    Verify Phone
                  </Button>
                )}
              </div>

              <div className="pt-3 border-t border-gray-100">
                <p className="text-sm text-gray-600">Member since</p>
                <p className="font-medium text-gray-900">
                  {helpers.formatDate(user?.createdAt, 'dd MMMM yyyy')}
                </p>
              </div>

              {user?.lastLoginAt && (
                <div>
                  <p className="text-sm text-gray-600">Last login</p>
                  <p className="font-medium text-gray-900">
                    {helpers.formatRelativeTime(user?.lastLoginAt)}
                  </p>
                </div>
              )}
            </div>
          )}
        </Card>
      </div>

      {/* Email Verification Modal */}
      <Modal
        isOpen={verifyingEmail}
        onClose={() => setVerifyingEmail(false)}
        title="Verify Email Address"
      >
        <OTPVerification
          contact={user?.email}
          type={OTP_TYPES.EMAIL_VERIFICATION}
          onVerified={handleEmailVerified}
          onResend={handleSendEmailOTP}
        />
      </Modal>

      {/* Phone Verification Modal */}
      <Modal
        isOpen={verifyingPhone}
        onClose={() => setVerifyingPhone(false)}
        title="Verify Phone Number"
      >
        <OTPVerification
          contact={user?.phone}
          type={OTP_TYPES.PHONE_VERIFICATION}
          onVerified={handlePhoneVerified}
          onResend={handleSendPhoneOTP}
        />
      </Modal>
    </div>
  );
}