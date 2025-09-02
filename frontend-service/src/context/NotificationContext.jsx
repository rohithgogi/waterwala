import React, { createContext, useContext, useState } from 'react';
import { CheckCircle, XCircle, AlertCircle, Info, X } from 'lucide-react';

const NotificationContext = createContext();

export const useNotification = () => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotification must be used within a NotificationProvider');
  }
  return context;
};

// Toast Component
const Toast = ({ notification, onRemove }) => {
  const { id, type, title, message, duration } = notification;

  // Auto remove toast after duration
  React.useEffect(() => {
    if (duration > 0) {
      const timer = setTimeout(() => {
        onRemove(id);
      }, duration);

      return () => clearTimeout(timer);
    }
  }, [id, duration, onRemove]);

  const getToastStyles = () => {
    switch (type) {
      case 'success':
        return {
          bgColor: 'bg-green-50',
          borderColor: 'border-green-200',
          iconColor: 'text-green-600',
          titleColor: 'text-green-800',
          messageColor: 'text-green-700',
          icon: CheckCircle
        };
      case 'error':
        return {
          bgColor: 'bg-red-50',
          borderColor: 'border-red-200',
          iconColor: 'text-red-600',
          titleColor: 'text-red-800',
          messageColor: 'text-red-700',
          icon: XCircle
        };
      case 'warning':
        return {
          bgColor: 'bg-yellow-50',
          borderColor: 'border-yellow-200',
          iconColor: 'text-yellow-600',
          titleColor: 'text-yellow-800',
          messageColor: 'text-yellow-700',
          icon: AlertCircle
        };
      case 'info':
      default:
        return {
          bgColor: 'bg-blue-50',
          borderColor: 'border-blue-200',
          iconColor: 'text-blue-600',
          titleColor: 'text-blue-800',
          messageColor: 'text-blue-700',
          icon: Info
        };
    }
  };

  const styles = getToastStyles();
  const Icon = styles.icon;

  return (
    <div className={`${styles.bgColor} ${styles.borderColor} border rounded-lg shadow-lg p-4 mb-4 max-w-md w-full transform transition-all duration-300 ease-in-out`}>
      <div className="flex items-start">
        <div className={`${styles.iconColor} mr-3 mt-0.5`}>
          <Icon className="h-5 w-5" />
        </div>
        <div className="flex-1">
          {title && (
            <h4 className={`${styles.titleColor} font-medium text-sm mb-1`}>
              {title}
            </h4>
          )}
          {message && (
            <p className={`${styles.messageColor} text-sm`}>
              {message}
            </p>
          )}
        </div>
        <button
          onClick={() => onRemove(id)}
          className={`${styles.iconColor} hover:opacity-75 ml-2`}
        >
          <X className="h-4 w-4" />
        </button>
      </div>
    </div>
  );
};

// Toast Container Component
const ToastContainer = ({ notifications, onRemove }) => {
  if (notifications.length === 0) return null;

  return (
    <div className="fixed top-4 right-4 z-50 space-y-2">
      {notifications.map((notification) => (
        <Toast
          key={notification.id}
          notification={notification}
          onRemove={onRemove}
        />
      ))}
    </div>
  );
};

export const NotificationProvider = ({ children }) => {
  const [notifications, setNotifications] = useState([]);

  // Generate unique ID for notifications
  const generateId = () => {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
  };

  // Add notification
  const addNotification = ({ type, title, message, duration = 5000 }) => {
    const id = generateId();
    const notification = {
      id,
      type,
      title,
      message,
      duration
    };

    setNotifications(prev => [...prev, notification]);
    return id;
  };

  // Remove notification
  const removeNotification = (id) => {
    setNotifications(prev => prev.filter(notification => notification.id !== id));
  };

  // Remove all notifications
  const clearNotifications = () => {
    setNotifications([]);
  };

  // Convenience methods for different notification types
  const showSuccess = (title, message, duration) => {
    return addNotification({ type: 'success', title, message, duration });
  };

  const showError = (title, message, duration = 8000) => {
    return addNotification({ type: 'error', title, message, duration });
  };

  const showWarning = (title, message, duration) => {
    return addNotification({ type: 'warning', title, message, duration });
  };

  const showInfo = (title, message, duration) => {
    return addNotification({ type: 'info', title, message, duration });
  };

  // Handle API response notifications
  const handleApiResponse = (response, successMessage = 'Operation successful') => {
    if (response.success) {
      showSuccess('Success', response.message || successMessage);
    } else {
      showError('Error', response.message || 'An error occurred');
    }
  };

  // Handle API errors
  const handleApiError = (error) => {
    const errorMessage = error.response?.data?.message ||
                        error.message ||
                        'An unexpected error occurred';

    showError('Error', errorMessage);
  };

  const value = {
    notifications,
    addNotification,
    removeNotification,
    clearNotifications,
    showSuccess,
    showError,
    showWarning,
    showInfo,
    handleApiResponse,
    handleApiError
  };

  return (
    <NotificationContext.Provider value={value}>
      {children}
      <ToastContainer
        notifications={notifications}
        onRemove={removeNotification}
      />
    </NotificationContext.Provider>
  );
};