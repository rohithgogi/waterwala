// src/components/layout/Dashboard.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Menu,
  X,
  Home,
  User,
  MapPin,
  ShoppingCart,
  Package,
  Store,
  Users,
  BarChart3,
  Settings,
  Bell,
  Search,
  Droplets,
  Phone,
  LogOut,
  ChevronDown
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useNotification } from '../../context/NotificationContext';

const Dashboard = ({ children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [profileDropdownOpen, setProfileDropdownOpen] = useState(false);

  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const { showSuccess } = useNotification();

  // Navigation items based on user role
  const getNavItems = () => {
    const baseItems = [
      { id: 'dashboard', label: 'Dashboard', icon: Home, path: '/dashboard' }
    ];

    if (user?.role === 'CUSTOMER') {
      return [
        ...baseItems,
        { id: 'products', label: 'Browse Products', icon: Package, path: '/products' },
        { id: 'orders', label: 'My Orders', icon: ShoppingCart, path: '/orders' },
        { id: 'addresses', label: 'My Addresses', icon: MapPin, path: '/addresses' },
        { id: 'profile', label: 'Profile', icon: User, path: '/profile' }
      ];
    }

    if (user?.role === 'BUSINESS_OWNER') {
      return [
        ...baseItems,
        { id: 'business-dashboard', label: 'Business Dashboard', icon: Store, path: '/business/dashboard' },
        { id: 'business-profile', label: 'Business Profile', icon: Store, path: '/business/profile' },
        { id: 'products-manage', label: 'Manage Products', icon: Package, path: '/business/products' },
        { id: 'orders-manage', label: 'Manage Orders', icon: ShoppingCart, path: '/business/orders' },
        { id: 'analytics', label: 'Analytics', icon: BarChart3, path: '/business/analytics' }
      ];
    }

    if (user?.role === 'ADMIN') {
      return [
        ...baseItems,
        { id: 'admin-dashboard', label: 'Admin Dashboard', icon: Home, path: '/admin/dashboard' },
        { id: 'users', label: 'User Management', icon: Users, path: '/admin/users' },
        { id: 'businesses', label: 'Business Management', icon: Store, path: '/admin/businesses' },
        { id: 'system-analytics', label: 'System Analytics', icon: BarChart3, path: '/admin/analytics' },
        { id: 'settings', label: 'Settings', icon: Settings, path: '/admin/settings' }
      ];
    }

    return baseItems;
  };

  const navItems = getNavItems();
  const currentPath = window.location.pathname;

  const handleNavigation = (path) => {
    navigate(path);
    setSidebarOpen(false);
  };

  const handleLogout = async () => {
    await logout();
    showSuccess('Logged Out', 'You have been successfully logged out');
    navigate('/login');
  };

  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good Morning';
    if (hour < 17) return 'Good Afternoon';
    return 'Good Evening';
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Mobile sidebar backdrop */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <div className={`fixed inset-y-0 left-0 z-50 w-64 bg-white border-r border-gray-200 transform transition-transform duration-200 ease-in-out lg:translate-x-0 ${
        sidebarOpen ? 'translate-x-0' : '-translate-x-full'
      } lg:static lg:inset-0`}>

        {/* Logo */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <div className="flex items-center space-x-3">
            <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
              <Droplets className="h-5 w-5 text-white" />
            </div>
            <span className="text-xl font-bold text-gray-900">Waterwala</span>
          </div>
          <button
            onClick={() => setSidebarOpen(false)}
            className="lg:hidden p-1 rounded-md hover:bg-gray-100"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* User Info Section */}
        <div className="p-4 border-b border-gray-200">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
              <span className="text-white font-medium text-sm">
                {user?.firstName?.charAt(0)}{user?.lastName?.charAt(0)}
              </span>
            </div>
            <div className="flex-1">
              <p className="text-sm font-medium text-gray-900">
                {user?.firstName} {user?.lastName}
              </p>
              <p className="text-xs text-gray-500 capitalize">
                {user?.role?.replace('_', ' ').toLowerCase()}
              </p>
            </div>
          </div>
          <div className="mt-2 text-xs text-gray-600">
            {getGreeting()}, {user?.firstName}!
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-4 space-y-1">
          {navItems.map((item) => {
            const isActive = currentPath === item.path ||
                           (item.path !== '/dashboard' && currentPath.startsWith(item.path));

            return (
              <button
                key={item.id}
                onClick={() => handleNavigation(item.path)}
                className={`w-full flex items-center space-x-3 px-3 py-2 rounded-lg text-left transition-colors duration-200 ${
                  isActive
                    ? 'bg-blue-50 text-blue-700 border border-blue-200'
                    : 'text-gray-700 hover:bg-gray-100'
                }`}
              >
                <item.icon className={`h-5 w-5 ${isActive ? 'text-blue-600' : 'text-gray-500'}`} />
                <span className="font-medium">{item.label}</span>
              </button>
            );
          })}
        </nav>

        {/* Logout Button */}
        <div className="p-4 border-t border-gray-200">
          <button
            onClick={handleLogout}
            className="w-full flex items-center space-x-2 text-red-600 hover:bg-red-50 p-2 rounded-lg transition-colors"
          >
            <LogOut className="h-4 w-4" />
            <span className="text-sm font-medium">Logout</span>
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="lg:ml-64 flex flex-col min-h-screen">
        {/* Header */}
        <header className="bg-white border-b border-gray-200 px-6 py-4 sticky top-0 z-30">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <button
                onClick={() => setSidebarOpen(true)}
                className="lg:hidden p-2 rounded-md hover:bg-gray-100"
              >
                <Menu className="h-5 w-5" />
              </button>

              {/* Search Bar */}
              <div className="relative hidden md:block">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                <input
                  type="text"
                  placeholder="Search..."
                  className="pl-10 pr-4 py-2 w-80 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>

            <div className="flex items-center space-x-4">
              {/* Notifications */}
              <button className="relative p-2 rounded-full hover:bg-gray-100">
                <Bell className="h-5 w-5 text-gray-600" />
                <span className="absolute -top-1 -right-1 h-4 w-4 bg-red-500 rounded-full text-xs text-white flex items-center justify-center">
                  3
                </span>
              </button>

              {/* User Profile Dropdown */}
              <div className="relative">
                <button
                  onClick={() => setProfileDropdownOpen(!profileDropdownOpen)}
                  className="flex items-center space-x-2 p-2 rounded-lg hover:bg-gray-100 transition-colors"
                >
                  <div className="w-8 h-8 bg-gradient-to-r from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
                    <span className="text-white font-medium text-xs">
                      {user?.firstName?.charAt(0)}{user?.lastName?.charAt(0)}
                    </span>
                  </div>
                  <ChevronDown className="h-4 w-4 text-gray-500" />
                </button>

                {/* Profile Dropdown Menu */}
                {profileDropdownOpen && (
                  <div className="absolute right-0 mt-2 w-48 bg-white border border-gray-200 rounded-lg shadow-lg py-1 z-50">
                    <div className="px-4 py-2 border-b border-gray-200">
                      <p className="text-sm font-medium text-gray-900">
                        {user?.firstName} {user?.lastName}
                      </p>
                      <p className="text-xs text-gray-500">{user?.email}</p>
                    </div>
                    <button
                      onClick={() => {
                        handleNavigation('/profile');
                        setProfileDropdownOpen(false);
                      }}
                      className="flex items-center space-x-2 w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                    >
                      <User className="h-4 w-4" />
                      <span>Profile Settings</span>
                    </button>
                    <button
                      onClick={() => {
                        handleLogout();
                        setProfileDropdownOpen(false);
                      }}
                      className="flex items-center space-x-2 w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                    >
                      <LogOut className="h-4 w-4" />
                      <span>Logout</span>
                    </button>
                  </div>
                )}
              </div>

              {/* Quick Contact */}
              <div className="hidden xl:flex items-center space-x-3 text-sm text-gray-600 pl-4 border-l border-gray-200">
                <Phone className="h-4 w-4" />
                <span>+91 98765 43210</span>
              </div>
            </div>
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 p-6">
          {children}
        </main>
      </div>

      {/* Click outside handler for profile dropdown */}
      {profileDropdownOpen && (
        <div
          className="fixed inset-0 z-40"
          onClick={() => setProfileDropdownOpen(false)}
        />
      )}
    </div>
  );
};

export default Dashboard;