import { format, formatDistanceToNow, isValid } from 'date-fns';

export const helpers = {
  // Format date
  formatDate: (date, formatStr = 'dd MMM yyyy') => {
    if (!date) return '-';
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    return isValid(dateObj) ? format(dateObj, formatStr) : '-';
  },

  // Format relative time
  formatRelativeTime: (date) => {
    if (!date) return '-';
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    return isValid(dateObj) ? formatDistanceToNow(dateObj, { addSuffix: true }) : '-';
  },

  // Format phone number
  formatPhone: (phone) => {
    if (!phone) return '';
    const cleaned = phone.replace(/\D/g, '');
    if (cleaned.length === 10) {
      return `+91 ${cleaned.slice(0, 5)} ${cleaned.slice(5)}`;
    }
    return phone;
  },

  // Generate device ID
  generateDeviceId: () => {
    return `web_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  },

  // Get user initials
  getUserInitials: (firstName, lastName) => {
    const first = firstName?.charAt(0)?.toUpperCase() || '';
    const last = lastName?.charAt(0)?.toUpperCase() || '';
    return `${first}${last}` || '??';
  },

  // Get address type icon
  getAddressTypeIcon: (type) => {
    const icons = {
      HOME: '🏠',
      OFFICE: '🏢',
      OTHER: '📍'
    };
    return icons[type] || '📍';
  },

  // Get status color
  getStatusColor: (status) => {
    const colors = {
      ACTIVE: 'bg-green-100 text-green-800',
      INACTIVE: 'bg-gray-100 text-gray-800',
      SUSPENDED: 'bg-red-100 text-red-800',
      PENDING_VERIFICATION: 'bg-yellow-100 text-yellow-800'
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  },

  // Truncate text
  truncate: (text, length = 50) => {
    if (!text) return '';
    return text.length > length ? `${text.substring(0, length)}...` : text;
  },

  // Debounce function
  debounce: (func, wait = 300) => {
    let timeout;
    return function executedFunction(...args) {
      const later = () => {
        clearTimeout(timeout);
        func(...args);
      };
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
    };
  },

  // Copy to clipboard
  copyToClipboard: async (text) => {
    try {
      await navigator.clipboard.writeText(text);
      return true;
    } catch (err) {
      console.error('Failed to copy:', err);
      return false;
    }
  },

  // Check if mobile
  isMobile: () => {
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
      navigator.userAgent
    );
  }
};