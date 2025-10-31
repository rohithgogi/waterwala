import React from 'react';
import { useNavigate } from 'react-router-dom';
import { User, MapPin, Mail, Phone, CheckCircle, XCircle } from 'lucide-react';
import { Card } from '../components/common/Card';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { useAuth } from '../context/AuthContext';
import { useAddresses } from '../hooks/useAddresses';
import { helpers } from '../utils';

export default function Dashboard() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { addresses, defaultAddress } = useAddresses(user?.id);

  const stats = [
    {
      label: 'Profile Completion',
      value: calculateProfileCompletion(user),
      icon: User,
      color: 'bg-blue-500'
    },
    {
      label: 'Saved Addresses',
      value: addresses.length,
      icon: MapPin,
      color: 'bg-green-500'
    },
    {
      label: 'Account Status',
      value: user?.status,
      icon: user?.status === 'ACTIVE' ? CheckCircle : XCircle,
      color: user?.status === 'ACTIVE' ? 'bg-green-500' : 'bg-yellow-500'
    }
  ];

  function calculateProfileCompletion(user) {
    if (!user) return 0;
    let completed = 0;
    const total = 6;

    if (user.firstName) completed++;
    if (user.lastName) completed++;
    if (user.email) completed++;
    if (user.phone) completed++;
    if (user.emailVerified) completed++;
    if (user.phoneVerified) completed++;

    return Math.round((completed / total) * 100);
  }

  return (
    <div className="space-y-6">
      {/* Welcome Section */}
      <div className="bg-gradient-to-r from-blue-600 to-indigo-600 rounded-xl shadow-lg p-8 text-white">
        <h1 className="text-3xl font-bold mb-2">
          Welcome back, {user?.firstName}! 👋
        </h1>
        <p className="text-blue-100">
          Here's what's happening with your account today
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid md:grid-cols-3 gap-6">
        {stats.map((stat, index) => (
          <Card key={index} className="hover:shadow-xl transition-shadow">
            <div className="flex items-center gap-4">
              <div className={`${stat.color} w-12 h-12 rounded-lg flex items-center justify-center text-white`}>
                <stat.icon className="w-6 h-6" />
              </div>
              <div>
                <p className="text-sm text-gray-600">{stat.label}</p>
                <p className="text-2xl font-bold text-gray-900">
                  {typeof stat.value === 'number' ? stat.value : stat.value}
                  {stat.label === 'Profile Completion' && '%'}
                </p>
              </div>
            </div>
          </Card>
        ))}
      </div>

      <div className="grid md:grid-cols-2 gap-6">
        {/* Account Overview */}
        <Card
          title="Account Overview"
          subtitle="Your profile information"
        >
          <div className="space-y-4">
            <div className="flex items-center justify-between py-3 border-b border-gray-100">
              <div className="flex items-center gap-3">
                <Mail className="w-5 h-5 text-gray-400" />
                <div>
                  <p className="text-sm text-gray-600">Email</p>
                  <p className="font-medium text-gray-900">{user?.email}</p>
                </div>
              </div>
              <Badge variant={user?.emailVerified ? 'success' : 'warning'}>
                {user?.emailVerified ? 'Verified' : 'Not Verified'}
              </Badge>
            </div>

            <div className="flex items-center justify-between py-3 border-b border-gray-100">
              <div className="flex items-center gap-3">
                <Phone className="w-5 h-5 text-gray-400" />
                <div>
                  <p className="text-sm text-gray-600">Phone</p>
                  <p className="font-medium text-gray-900">
                    {helpers.formatPhone(user?.phone)}
                  </p>
                </div>
              </div>
              <Badge variant={user?.phoneVerified ? 'success' : 'warning'}>
                {user?.phoneVerified ? 'Verified' : 'Not Verified'}
              </Badge>
            </div>

            <div className="flex items-center justify-between py-3">
              <div className="flex items-center gap-3">
                <User className="w-5 h-5 text-gray-400" />
                <div>
                  <p className="text-sm text-gray-600">Role</p>
                  <p className="font-medium text-gray-900">{user?.role}</p>
                </div>
              </div>
            </div>

            <Button
              onClick={() => navigate('/profile')}
              variant="outline"
              className="w-full"
            >
              View Profile
            </Button>
          </div>
        </Card>

        {/* Default Address */}
        <Card
          title="Default Address"
          subtitle="Your primary delivery location"
          actions={
            <Button
              onClick={() => navigate('/addresses')}
              variant="ghost"
              size="sm"
            >
              View All
            </Button>
          }
        >
          {defaultAddress ? (
            <div className="space-y-4">
              <div className="flex items-start gap-3">
                <div className="w-10 h-10 bg-blue-50 rounded-lg flex items-center justify-center text-2xl">
                  {helpers.getAddressTypeIcon(defaultAddress.type)}
                </div>
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <h4 className="font-semibold text-gray-900">
                      {defaultAddress.type}
                    </h4>
                    <Badge variant="info">Default</Badge>
                  </div>
                  <p className="text-sm text-gray-600">
                    {defaultAddress.addressLine1}
                    {defaultAddress.addressLine2 && `, ${defaultAddress.addressLine2}`}
                  </p>
                  <p className="text-sm text-gray-600">
                    {defaultAddress.city}, {defaultAddress.state} - {defaultAddress.pincode}
                  </p>
                </div>
              </div>

              <Button
                onClick={() => navigate('/addresses')}
                variant="outline"
                className="w-full"
              >
                Manage Addresses
              </Button>
            </div>
          ) : (
            <div className="text-center py-8">
              <MapPin className="w-12 h-12 text-gray-300 mx-auto mb-3" />
              <p className="text-gray-600 mb-4">No default address set</p>
              <Button onClick={() => navigate('/addresses')}>
                Add Address
              </Button>
            </div>
          )}
        </Card>
      </div>

      {/* Quick Actions */}
      {(!user?.emailVerified || !user?.phoneVerified) && (
        <Card title="Action Required">
          <div className="space-y-3">
            {!user?.emailVerified && (
              <div className="flex items-center justify-between p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                <div className="flex items-center gap-3">
                  <Mail className="w-5 h-5 text-yellow-600" />
                  <p className="text-sm text-yellow-800">
                    Please verify your email address
                  </p>
                </div>
                <Button
                  onClick={() => navigate('/profile')}
                  variant="outline"
                  size="sm"
                >
                  Verify Now
                </Button>
              </div>
            )}

            {!user?.phoneVerified && (
              <div className="flex items-center justify-between p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                <div className="flex items-center gap-3">
                  <Phone className="w-5 h-5 text-yellow-600" />
                  <p className="text-sm text-yellow-800">
                    Please verify your phone number
                  </p>
                </div>
                <Button
                  onClick={() => navigate('/profile')}
                  variant="outline"
                  size="sm"
                >
                  Verify Now
                </Button>
              </div>
            )}
          </div>
        </Card>
      )}
    </div>
  );
}
