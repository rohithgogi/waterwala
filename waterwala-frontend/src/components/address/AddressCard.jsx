import React, { useState } from 'react';
import { MapPin, Edit2, Trash2, Star, StarOff } from 'lucide-react';
import { Card } from '../common/Card';
import { Badge } from '../common/Badge';
import { Button } from '../common/Button';
import { helpers } from '../../utils';

export const AddressCard = ({
  address,
  onEdit,
  onDelete,
  onSetDefault
}) => {
  const [deleting, setDeleting] = useState(false);

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this address?')) {
      setDeleting(true);
      try {
        await onDelete(address.id);
      } finally {
        setDeleting(false);
      }
    }
  };

  const handleSetDefault = async () => {
    if (!address.isDefault) {
      await onSetDefault(address.id);
    }
  };

  return (
    <Card className="hover:shadow-xl transition-shadow">
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-blue-50 rounded-lg flex items-center justify-center text-2xl">
            {helpers.getAddressTypeIcon(address.type)}
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h3 className="font-semibold text-gray-900">{address.type}</h3>
              {address.isDefault && (
                <Badge variant="success">
                  <Star className="w-3 h-3 mr-1" />
                  Default
                </Badge>
              )}
            </div>
          </div>
        </div>

        <div className="flex gap-2">
          <button
            onClick={() => onEdit(address)}
            className="p-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
          >
            <Edit2 className="w-4 h-4" />
          </button>
          <button
            onClick={handleDelete}
            disabled={deleting}
            className="p-2 text-gray-600 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors disabled:opacity-50"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>

      <div className="space-y-2 mb-4">
        <p className="text-gray-700">{address.addressLine1}</p>
        {address.addressLine2 && (
          <p className="text-gray-700">{address.addressLine2}</p>
        )}
        {address.landmark && (
          <p className="text-gray-600 text-sm">
            <MapPin className="w-3 h-3 inline mr-1" />
            Near: {address.landmark}
          </p>
        )}
        <p className="text-gray-700">
          {address.city}, {address.state} - {address.pincode}
        </p>
      </div>

      {!address.isDefault && (
        <Button
          onClick={handleSetDefault}
          variant="outline"
          size="sm"
          className="w-full"
          icon={Star}
        >
          Set as Default
        </Button>
      )}
    </Card>
  );
};