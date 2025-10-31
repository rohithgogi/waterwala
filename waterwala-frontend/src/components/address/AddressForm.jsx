import React from 'react';
import { useForm } from 'react-hook-form';
import { Input } from '../common/Input';
import { Button } from '../common/Button';
import { validators } from '../../utils';
import { ADDRESS_TYPES } from '../../utils/constants';

export const AddressForm = ({ address, onSubmit, onCancel }) => {
  const { register, handleSubmit, formState: { errors }, watch } = useForm({
    defaultValues: address || {
      type: ADDRESS_TYPES.HOME,
      country: 'India'
    }
  });

  const [submitting, setSubmitting] = React.useState(false);

  const handleFormSubmit = async (data) => {
    setSubmitting(true);
    try {
      await onSubmit(data);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      {/* Address Type */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Address Type
        </label>
        <div className="grid grid-cols-3 gap-3">
          {Object.values(ADDRESS_TYPES).map((type) => (
            <label
              key={type}
              className="relative flex items-center justify-center p-3 border-2 rounded-lg cursor-pointer hover:border-blue-500 transition-colors"
            >
              <input
                type="radio"
                value={type}
                {...register('type', { required: 'Address type is required' })}
                className="sr-only"
              />
              <div className="text-center">
                <span className="text-2xl mb-1 block">
                  {type === 'HOME' ? '🏠' : type === 'OFFICE' ? '🏢' : '📍'}
                </span>
                <span className="text-sm font-medium text-gray-700">{type}</span>
              </div>
            </label>
          ))}
        </div>
        {errors.type && (
          <p className="mt-1 text-sm text-red-600">{errors.type.message}</p>
        )}
      </div>

      {/* Address Line 1 */}
      <Input
        label="Address Line 1 *"
        placeholder="House/Flat No., Building Name"
        {...register('addressLine1', {
          required: 'Address line 1 is required',
          validate: (value) => validators.required(value, 'Address line 1')
        })}
        error={errors.addressLine1?.message}
      />

      {/* Address Line 2 */}
      <Input
        label="Address Line 2"
        placeholder="Street, Area, Colony"
        {...register('addressLine2')}
      />

      {/* Landmark */}
      <Input
        label="Landmark"
        placeholder="Near famous place/building"
        {...register('landmark')}
      />

      {/* City, State, Pincode */}
      <div className="grid grid-cols-3 gap-4">
        <Input
          label="City *"
          placeholder="City"
          {...register('city', {
            required: 'City is required',
            validate: (value) => validators.required(value, 'City')
          })}
          error={errors.city?.message}
        />

        <Input
          label="State *"
          placeholder="State"
          {...register('state', {
            required: 'State is required',
            validate: (value) => validators.required(value, 'State')
          })}
          error={errors.state?.message}
        />

        <Input
          label="Pincode *"
          placeholder="Pincode"
          {...register('pincode', {
            required: 'Pincode is required',
            validate: (value) => validators.pincode(value)
          })}
          error={errors.pincode?.message}
          maxLength={6}
        />
      </div>

      {/* Set as Default */}
      <div className="flex items-center gap-2">
        <input
          type="checkbox"
          id="isDefault"
          {...register('isDefault')}
          className="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
        />
        <label htmlFor="isDefault" className="text-sm text-gray-700">
          Set as default address
        </label>
      </div>

      {/* Actions */}
      <div className="flex gap-3 pt-4">
        <Button
          type="submit"
          loading={submitting}
          className="flex-1"
        >
          {address ? 'Update Address' : 'Add Address'}
        </Button>
        <Button
          type="button"
          variant="outline"
          onClick={onCancel}
          className="flex-1"
        >
          Cancel
        </Button>
      </div>
    </form>
  );
};