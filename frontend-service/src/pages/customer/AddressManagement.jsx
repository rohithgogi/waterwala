// src/pages/customer/AddressManagement.jsx
import React, { useState, useEffect } from 'react';
import {
  MapPin,
  Plus,
  Edit,
  Trash2,
  Home,
  Building,
  Briefcase,
  Star,
  Phone,
  User,
  Navigation,
  Check,
  X,
  AlertCircle
} from 'lucide-react';
import { useNotification } from '../../context/NotificationContext';

const AddressManagement = () => {
  const [addresses, setAddresses] = useState([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [editingAddress, setEditingAddress] = useState(null);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    type: 'HOME',
    recipientName: '',
    phone: '',
    addressLine1: '',
    addressLine2: '',
    landmark: '',
    city: '',
    state: '',
    pincode: '',
    isDefault: false
  });

  const { showSuccess, showError, showInfo } = useNotification();

  // Load addresses on component mount
  useEffect(() => {
    loadAddresses();
  }, []);

  const loadAddresses = async () => {
    setLoading(true);
    try {
      // Mock data - replace with actual API call
      setTimeout(() => {
        setAddresses([
          {
            id: 1,
            type: 'HOME',
            recipientName: 'Raju Gogi',
            phone: '9876543210',
            addressLine1: 'Flat 301, Sunshine Apartments',
            addressLine2: 'Sector 14, Phase 2',
            landmark: 'Near City Mall',
            city: 'Gurugram',
            state: 'Haryana',
            pincode: '122001',
            isDefault: true,
            createdAt: '2024-01-15'
          },
          {
            id: 2,
            type: 'OFFICE',
            recipientName: 'Raju Gogi',
            phone: '9876543210',
            addressLine1: 'Office 504, Tech Tower',
            addressLine2: 'Cyber City',
            landmark: 'Opposite Metro Station',
            city: 'Gurugram',
            state: 'Haryana',
            pincode: '122002',
            isDefault: false,
            createdAt: '2024-01-20'
          }
        ]);
        setLoading(false);
      }, 1000);
    } catch (error) {
      showError('Error', 'Failed to load addresses');
      setLoading(false);
    }
  };

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const validateForm = () => {
    if (!formData.recipientName.trim()) return 'Recipient name is required';
    if (!formData.phone.trim() || !/^[6-9]\d{9}$/.test(formData.phone)) return 'Valid phone number is required';
    if (!formData.addressLine1.trim()) return 'Address line 1 is required';
    if (!formData.city.trim()) return 'City is required';
    if (!formData.state.trim()) return 'State is required';
    if (!formData.pincode.trim() || !/^\d{6}$/.test(formData.pincode)) return 'Valid pincode is required';
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const error = validateForm();
    if (error) {
      showError('Validation Error', error);
      return;
    }

    setLoading(true);
    try {
      // Mock API call
      await new Promise(resolve => setTimeout(resolve, 1000));

      if (editingAddress) {
        // Update existing address
        setAddresses(prev => prev.map(addr =>
          addr.id === editingAddress.id
            ? { ...addr, ...formData, id: editingAddress.id }
            : addr
        ));
        showSuccess('Address Updated', 'Your address has been updated successfully');
      } else {
        // Add new address
        const newAddress = {
          ...formData,
          id: Date.now(),
          createdAt: new Date().toISOString().split('T')[0]
        };
        setAddresses(prev => [...prev, newAddress]);
        showSuccess('Address Added', 'New address has been added successfully');
      }

      resetForm();
    } catch (error) {
      showError('Error', 'Failed to save address');
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({
      type: 'HOME',
      recipientName: '',
      phone: '',
      addressLine1: '',
      addressLine2: '',
      landmark: '',
      city: '',
      state: '',
      pincode: '',
      isDefault: false
    });
    setShowAddForm(false);
    setEditingAddress(null);
  };

  const handleEdit = (address) => {
    setFormData(address);
    setEditingAddress(address);
    setShowAddForm(true);
  };

  const handleDelete = async (addressId) => {
    if (!window.confirm('Are you sure you want to delete this address?')) return;

    setLoading(true);
    try {
      // Mock API call
      await new Promise(resolve => setTimeout(resolve, 500));

      setAddresses(prev => prev.filter(addr => addr.id !== addressId));
      showSuccess('Address Deleted', 'Address has been removed successfully');
    } catch (error) {
      showError('Error', 'Failed to delete address');
    } finally {
      setLoading(false);
    }
  };

  const handleSetDefault = async (addressId) => {
    setLoading(true);
    try {
      // Mock API call
      await new Promise(resolve => setTimeout(resolve, 500));

      setAddresses(prev => prev.map(addr => ({
        ...addr,
        isDefault: addr.id === addressId
      })));
      showSuccess('Default Address', 'Default address updated successfully');
    } catch (error) {
      showError('Error', 'Failed to update default address');
    } finally {
      setLoading(false);
    }
  };

  const getAddressIcon = (type) => {
    switch (type) {
      case 'HOME': return <Home className="h-5 w-5" />;
      case 'OFFICE': return <Briefcase className="h-5 w-5" />;
      case 'OTHER': return <MapPin className="h-5 w-5" />;
      default: return <MapPin className="h-5 w-5" />;
    }
  };

  const getAddressTypeColor = (type) => {
    switch (type) {
      case 'HOME': return 'bg-blue-100 text-blue-800';
      case 'OFFICE': return 'bg-green-100 text-green-800';
      case 'OTHER': return 'bg-purple-100 text-purple-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading && addresses.length === 0) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600">Loading addresses...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Address Management</h1>
          <p className="text-gray-600 mt-1">Manage your delivery addresses for quick and easy ordering</p>
        </div>
        <button
          onClick={() => setShowAddForm(true)}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2"
        >
          <Plus className="h-4 w-4" />
          <span>Add Address</span>
        </button>
      </div>

      {/* Address Form Modal */}
      {showAddForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-gray-200">
              <div className="flex items-center justify-between">
                <h2 className="text-xl font-bold text-gray-900">
                  {editingAddress ? 'Edit Address' : 'Add New Address'}
                </h2>
                <button
                  onClick={resetForm}
                  className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                >
                  <X className="h-5 w-5" />
                </button>
              </div>
            </div>

            <form onSubmit={handleSubmit} className="p-6 space-y-6">
              {/* Address Type */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-3">Address Type</label>
                <div className="grid grid-cols-3 gap-3">
                  {['HOME', 'OFFICE', 'OTHER'].map((type) => (
                    <button
                      key={type}
                      type="button"
                      onClick={() => handleInputChange('type', type)}
                      className={`p-3 rounded-lg border-2 transition-all flex flex-col items-center space-y-2 ${
                        formData.type === type
                          ? 'border-blue-500 bg-blue-50 text-blue-700'
                          : 'border-gray-200 hover:border-gray-300'
                      }`}
                    >
                      {getAddressIcon(type)}
                      <span className="text-sm font-medium capitalize">{type.toLowerCase()}</span>
                    </button>
                  ))}
                </div>
              </div>

              {/* Recipient Details */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Recipient Name *</label>
                  <div className="relative">
                    <User className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                      type="text"
                      value={formData.recipientName}
                      onChange={(e) => handleInputChange('recipientName', e.target.value)}
                      placeholder="Full Name"
                      className="input-field pl-11"
                    />
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Phone Number *</label>
                  <div className="relative">
                    <Phone className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                      type="tel"
                      value={formData.phone}
                      onChange={(e) => handleInputChange('phone', e.target.value)}
                      placeholder="Mobile Number"
                      className="input-field pl-11"
                      maxLength="10"
                    />
                  </div>
                </div>
              </div>

              {/* Address Lines */}
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Address Line 1 *</label>
                  <input
                    type="text"
                    value={formData.addressLine1}
                    onChange={(e) => handleInputChange('addressLine1', e.target.value)}
                    placeholder="House/Flat No, Building Name"
                    className="input-field"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Address Line 2</label>
                  <input
                    type="text"
                    value={formData.addressLine2}
                    onChange={(e) => handleInputChange('addressLine2', e.target.value)}
                    placeholder="Street, Area, Locality"
                    className="input-field"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Landmark</label>
                  <input
                    type="text"
                    value={formData.landmark}
                    onChange={(e) => handleInputChange('landmark', e.target.value)}
                    placeholder="Nearby landmark (optional)"
                    className="input-field"
                  />
                </div>
              </div>

              {/* Location Details */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">City *</label>
                  <input
                    type="text"
                    value={formData.city}
                    onChange={(e) => handleInputChange('city', e.target.value)}
                    placeholder="City"
                    className="input-field"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">State *</label>
                  <input
                    type="text"
                    value={formData.state}
                    onChange={(e) => handleInputChange('state', e.target.value)}
                    placeholder="State"
                    className="input-field"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Pincode *</label>
                  <input
                    type="text"
                    value={formData.pincode}
                    onChange={(e) => handleInputChange('pincode', e.target.value)}
                    placeholder="Pincode"
                    className="input-field"
                    maxLength="6"
                  />
                </div>
              </div>

              {/* Default Address Checkbox */}
              <div className="flex items-center space-x-3">
                <input
                  type="checkbox"
                  id="isDefault"
                  checked={formData.isDefault}
                  onChange={(e) => handleInputChange('isDefault', e.target.checked)}
                  className="h-4 w-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                />
                <label htmlFor="isDefault" className="text-sm font-medium text-gray-700">
                  Set as default address
                </label>
              </div>

              {/* Form Actions */}
              <div className="flex items-center justify-end space-x-3 pt-6 border-t border-gray-200">
                <button
                  type="button"
                  onClick={resetForm}
                  className="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2"
                >
                  {loading ? (
                    <div className="spinner" />
                  ) : (
                    <>
                      <span>{editingAddress ? 'Update' : 'Save'} Address</span>
                      <Check className="h-4 w-4" />
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Addresses List */}
      {addresses.length === 0 ? (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-12 text-center">
          <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
            <MapPin className="h-8 w-8 text-gray-400" />
          </div>
          <h3 className="text-xl font-semibold text-gray-900 mb-4">No Addresses Found</h3>
          <p className="text-gray-600 mb-8 max-w-md mx-auto">
            You haven't added any delivery addresses yet. Add your first address to start ordering water.
          </p>
          <button
            onClick={() => setShowAddForm(true)}
            className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2 mx-auto"
          >
            <Plus className="h-5 w-5" />
            <span>Add Your First Address</span>
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {addresses.map((address) => (
            <div key={address.id} className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition-shadow">
              {/* Address Header */}
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center space-x-3">
                  <div className={`p-2 rounded-lg ${getAddressTypeColor(address.type)}`}>
                    {getAddressIcon(address.type)}
                  </div>
                  <div>
                    <div className="flex items-center space-x-2">
                      <h3 className="font-semibold text-gray-900 capitalize">
                        {address.type.toLowerCase()}
                      </h3>
                      {address.isDefault && (
                        <span className="bg-yellow-100 text-yellow-800 text-xs px-2 py-1 rounded-full flex items-center space-x-1">
                          <Star className="h-3 w-3" />
                          <span>Default</span>
                        </span>
                      )}
                    </div>
                    <p className="text-sm text-gray-600">{address.recipientName}</p>
                  </div>
                </div>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => handleEdit(address)}
                    className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                  >
                    <Edit className="h-4 w-4" />
                  </button>
                  <button
                    onClick={() => handleDelete(address.id)}
                    className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                </div>
              </div>

              {/* Address Details */}
              <div className="space-y-2 mb-4">
                <p className="text-gray-800">
                  {address.addressLine1}
                  {address.addressLine2 && `, ${address.addressLine2}`}
                </p>
                {address.landmark && (
                  <p className="text-sm text-gray-600">Near: {address.landmark}</p>
                )}
                <p className="text-sm text-gray-600">
                  {address.city}, {address.state} - {address.pincode}
                </p>
                <div className="flex items-center space-x-4 text-sm text-gray-600">
                  <div className="flex items-center space-x-1">
                    <Phone className="h-4 w-4" />
                    <span>{address.phone}</span>
                  </div>
                </div>
              </div>

              {/* Address Actions */}
              <div className="flex items-center justify-between pt-4 border-t border-gray-100">
                <div className="text-xs text-gray-500">
                  Added on {new Date(address.createdAt).toLocaleDateString()}
                </div>
                {!address.isDefault && (
                  <button
                    onClick={() => handleSetDefault(address.id)}
                    className="text-sm text-blue-600 hover:text-blue-700 font-medium"
                  >
                    Set as Default
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Quick Tips */}
      <div className="bg-blue-50 border border-blue-200 rounded-xl p-6">
        <div className="flex items-start space-x-3">
          <AlertCircle className="h-5 w-5 text-blue-600 mt-0.5" />
          <div>
            <h3 className="font-semibold text-blue-900 mb-2">Address Tips</h3>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Add complete address with house/flat number for accurate delivery</li>
              <li>• Include nearby landmarks to help delivery partners find your location</li>
              <li>• Set a default address for faster checkout experience</li>
              <li>• You can add multiple addresses for home, office, and other locations</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddressManagement;