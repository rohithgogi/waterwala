import React, { useState } from 'react';
import { Plus, MapPin } from 'lucide-react';
import { Button } from '../components/common/Button';
import { Modal } from '../components/common/Modal';
import { EmptyState } from '../components/common/EmptyState';
import { SkeletonLoader } from '../components/common/SkeletonLoader';
import { AddressCard } from '../components/address/AddressCard';
import { AddressForm } from '../components/address/AddressForm';
import { useAuth } from '../context/AuthContext';
import { useAddresses } from '../hooks/useAddresses';

export default function Addresses() {
  const { user } = useAuth();
  const {
    addresses,
    loading,
    addAddress,
    updateAddress,
    deleteAddress,
    setDefaultAddress
  } = useAddresses(user?.id);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingAddress, setEditingAddress] = useState(null);

  const handleAddAddress = async (addressData) => {
    await addAddress(addressData);
    setIsModalOpen(false);
  };

  const handleUpdateAddress = async (addressData) => {
    await updateAddress(editingAddress.id, addressData);
    setEditingAddress(null);
  };

  const handleEdit = (address) => {
    setEditingAddress(address);
  };

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">My Addresses</h1>
            <p className="text-gray-600 mt-1">Manage your delivery addresses</p>
          </div>
        </div>
        <div className="grid md:grid-cols-2 gap-6">
          <SkeletonLoader type="card" count={2} />
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">My Addresses</h1>
          <p className="text-gray-600 mt-1">
            Manage your delivery addresses
          </p>
        </div>
        <Button
          onClick={() => setIsModalOpen(true)}
          icon={Plus}
        >
          Add Address
        </Button>
      </div>

      {addresses.length === 0 ? (
        <EmptyState
          icon={MapPin}
          title="No addresses yet"
          message="Add your first address to get started with deliveries"
          action={() => setIsModalOpen(true)}
          actionLabel="Add Address"
        />
      ) : (
        <div className="grid md:grid-cols-2 gap-6">
          {addresses.map((address) => (
            <AddressCard
              key={address.id}
              address={address}
              onEdit={handleEdit}
              onDelete={deleteAddress}
              onSetDefault={setDefaultAddress}
            />
          ))}
        </div>
      )}

      {/* Add Address Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Add New Address"
        size="lg"
      >
        <AddressForm
          onSubmit={handleAddAddress}
          onCancel={() => setIsModalOpen(false)}
        />
      </Modal>

      {/* Edit Address Modal */}
      <Modal
        isOpen={!!editingAddress}
        onClose={() => setEditingAddress(null)}
        title="Edit Address"
        size="lg"
      >
        {editingAddress && (
          <AddressForm
            address={editingAddress}
            onSubmit={handleUpdateAddress}
            onCancel={() => setEditingAddress(null)}
          />
        )}
      </Modal>
    </div>
  );
}