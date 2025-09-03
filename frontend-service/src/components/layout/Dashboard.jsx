// src/components/layout/Dashboard.jsx - Enhanced Version with Fixed Alignment
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
  ChevronDown,
  Sun,
  Moon,
  Maximize,
  Minimize
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useNotification } from '../../context/NotificationContext';

const Dashboard = ({ children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [profileDropdownOpen, setProfileDropdownOpen] = useState(false);
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [isDarkMode, setIsDarkMode] = useState(false);

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

  const sidebarWidth = sidebarCollapsed ? 'w-20' : 'w-64';

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-50">
      {/* Mobile sidebar backdrop */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <div className={`fixed inset-y-0 left-0 z-50 ${sidebarWidth} bg-gradient-to-b from-blue-900 to-blue-800 shadow-2xl transform transition-all duration-300 ease-in-out lg:translate-x-0 ${
        sidebarOpen ? 'translate-x-0' : '-translate-x-full'
      } lg:static lg:inset-0`}>

        {/* Logo */}
        <div className="flex items-center justify-between p-6 border-b border-blue-700/50">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 bg-gradient-to-r from-blue-400 to-cyan-400 rounded-xl flex items-center justify-center shadow-lg">
              <Droplets className="h-6 w-6 text-white" />
            </div>
            {!sidebarCollapsed && (
              <span className="text-xl font-bold text-white">
                Waterwala
              </span>
            )}
          </div>
          <div className="flex items-center space-x-2">
            <button
              onClick={() => setSidebarCollapsed(!sidebarCollapsed)}
              className="hidden lg:block p-2 rounded-lg hover:bg-blue-700/50 text-blue-200 hover:text-white transition-colors"
            >
              {sidebarCollapsed ? <Maximize className="h-4 w-4" /> : <Minimize className="h-4 w-4" />}
            </button>
            <button
              onClick={() => setSidebarOpen(false)}
              className="lg:hidden p-2 rounded-lg hover:bg-blue-700/50 text-blue-200 hover:text-white transition-colors"
            >
              <X className="h-5 w-5" />
            </button>
          </div>
        </div>

        {/* User Info Section */}
        {!sidebarCollapsed && (
          <div className="p-6 border-b border-blue-700/50">
            <div className="flex items-center space-x-3">
              <div className="w-12 h-12 bg-gradient-to-r from-cyan-400 to-blue-400 rounded-xl flex items-center justify-center shadow-lg border-2 border-white/20">
                <span className="text-white font-semibold text-sm">
                  {user?.firstName?.charAt(0)}{user?.lastName?.charAt(0)}
                </span>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-semibold text-white truncate">
                  {user?.firstName} {user?.lastName}
                </p>
                <p className="text-xs text-blue-200 capitalize truncate">
                  {user?.role?.replace('_', ' ').toLowerCase()}
                </p>
              </div>
            </div>
            <div className="mt-3 px-3 py-2 bg-blue-800/50 rounded-lg">
              <p className="text-xs text-blue-100">
                {getGreeting()}, {user?.firstName}! 👋
              </p>
            </div>
          </div>
        )}

        {/* Navigation */}
        <nav className="flex-1 p-4 space-y-2">
          {navItems.map((item) => {
            const isActive = currentPath === item.path ||
                           (item.path !== '/dashboard' && currentPath.startsWith(item.path));

            return (
              <button
                key={item.id}
                onClick={() => handleNavigation(item.path)}
                className={`w-full flex items-center space-x-3 px-4 py-3 rounded-xl text-left transition-all duration-200 group ${
                  isActive
                    ? 'bg-gradient-to-r from-blue-600 to-blue-500 text-white shadow-lg transform scale-105'
                    : 'text-blue-100 hover:bg-blue-700/50 hover:text-white hover:transform hover:scale-105'
                }`}
                title={sidebarCollapsed ? item.label : ''}
              >
                <item.icon className={`h-5 w-5 transition-colors ${
                  isActive ? 'text-white' : 'text-blue-300 group-hover:text-white'
                }`} />
                {!sidebarCollapsed && (
                  <span className="font-medium transition-colors">{item.label}</span>
                )}
                {isActive && !sidebarCollapsed && (
                  <div className="ml-auto w-2 h-2 bg-cyan-300 rounded-full animate-pulse"></div>
                )}
              </button>
            );
          })}
        </nav>

        {/* Logout Button */}
        <div className="p-4 border-t border-blue-700/50">
          <button
            onClick={handleLogout}
            className="w-full flex items-center space-x-3 text-red-300 hover:bg-red-500/20 hover:text-red-200 p-3 rounded-xl transition-all duration-200 group"
            title={sidebarCollapsed ? 'Logout' : ''}
          >
            <LogOut className="h-5 w-5" />
            {!sidebarCollapsed && (
              <span className="text-sm font-medium">Logout</span>
            )}
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className={`${sidebarCollapsed ? 'lg:ml-20' : 'lg:ml-64'} flex flex-col min-h-screen transition-all duration-300`}>
        {/* Header */}
        <header className="bg-white/80 backdrop-blur-sm border-b border-blue-100 px-6 py-4 sticky top-0 z-30 shadow-sm">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <button
                onClick={() => setSidebarOpen(true)}
                className="lg:hidden p-2 rounded-xl hover:bg-blue-50 text-blue-600 transition-colors"
              >
                <Menu className="h-6 w-6" />
              </button>

              {/* Search Bar */}
              <div className="relative hidden md:block">
                <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 h-5 w-5 text-blue-400" />
                <input
                  type="text"
                  placeholder="Search products, orders..."
                  className="pl-12 pr-4 py-3 w-96 border-2 border-blue-100 rounded-2xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-blue-50/50 transition-all duration-200 text-gray-700 placeholder-blue-400"
                />
              </div>
            </div>

            <div className="flex items-center space-x-4">
              {/* Notifications */}
              <button className="relative p-3 rounded-2xl hover:bg-blue-50 text-blue-600 transition-colors">
                <Bell className="h-6 w-6" />
                <span className="absolute -top-1 -right-1 h-6 w-6 bg-gradient-to-r from-red-500 to-pink-500 rounded-full text-xs text-white flex items-center justify-center shadow-lg animate-pulse">
                  3
                </span>
              </button>

              {/* User Profile Dropdown */}
              <div className="relative">
                <button
                  onClick={() => setProfileDropdownOpen(!profileDropdownOpen)}
                  className="flex items-center space-x-3 p-2 rounded-2xl hover:bg-blue-50 transition-colors border-2 border-transparent hover:border-blue-200"
                >
                  <div className="w-12 h-12 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-2xl flex items-center justify-center shadow-lg border-2 border-white">
                    <span className="text-white font-semibold text-sm">
                      {user?.firstName?.charAt(0)}{user?.lastName?.charAt(0)}
                    </span>
                  </div>
                  <div className="hidden sm:block text-left">
                    <p className="text-sm font-semibold text-gray-900">
                      {user?.firstName} {user?.lastName}
                    </p>
                    <p className="text-xs text-blue-600 capitalize font-medium">
                      {user?.role?.replace('_', ' ').toLowerCase()}
                    </p>
                  </div>
                  <ChevronDown className="h-5 w-5 text-blue-500" />
                </button>

                {/* Profile Dropdown Menu */}
                {profileDropdownOpen && (
                  <div className="absolute right-0 mt-3 w-64 bg-white rounded-2xl shadow-2xl border-2 border-blue-100 py-2 z-50 backdrop-blur-sm">
                    <div className="px-6 py-4 border-b border-blue-100">
                      <p className="text-sm font-semibold text-gray-900">
                        {user?.firstName} {user?.lastName}
                      </p>
                      <p className="text-xs text-blue-600 font-medium">{user?.email}</p>
                    </div>
                    <button
                      onClick={() => {
                        handleNavigation('/profile');
                        setProfileDropdownOpen(false);
                      }}
                      className="flex items-center space-x-3 w-full px-6 py-3 text-sm text-gray-700 hover:bg-blue-50 transition-colors"
                    >
                      <User className="h-4 w-4 text-blue-600" />
                      <span>Profile Settings</span>
                    </button>
                    <button
                      onClick={() => {
                        handleNavigation('/settings');
                        setProfileDropdownOpen(false);
                      }}
                      className="flex items-center space-x-3 w-full px-6 py-3 text-sm text-gray-700 hover:bg-blue-50 transition-colors"
                    >
                      <Settings className="h-4 w-4 text-blue-600" />
                      <span>Account Settings</span>
                    </button>
                    <div className="border-t border-blue-100 my-2"></div>
                    <button
                      onClick={() => {
                        handleLogout();
                        setProfileDropdownOpen(false);
                      }}
                      className="flex items-center space-x-3 w-full px-6 py-3 text-sm text-red-600 hover:bg-red-50 transition-colors"
                    >
                      <LogOut className="h-4 w-4" />
                      <span>Logout</span>
                    </button>
                  </div>
                )}
              </div>

              {/* Quick Contact */}
              <div className="hidden xl:flex items-center space-x-3 text-sm pl-6 border-l-2 border-blue-200">
                <div className="p-2 bg-gradient-to-r from-green-400 to-green-500 rounded-xl shadow-lg">
                  <Phone className="h-5 w-5 text-white" />
                </div>
                <div>
                  <p className="text-xs font-medium text-gray-500">24/7 Support</p>
                  <p className="font-semibold text-blue-600">+91 98765 43210</p>
                </div>
              </div>
            </div>
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 p-6">
          <div className="max-w-7xl mx-auto">
            {children}
          </div>
        </main>

        {/* Footer */}
        <footer className="bg-gradient-to-r from-blue-600 to-blue-700 text-white px-6 py-6 border-t border-blue-500">
          <div className="max-w-7xl mx-auto flex items-center justify-between text-sm">
            <div className="flex items-center space-x-6">
              <span className="font-medium">© 2025 Waterwala. All rights reserved.</span>
              <div className="hidden md:flex items-center space-x-4">
                <a href="#" className="hover:text-blue-200 transition-colors">Privacy Policy</a>
                <a href="#" className="hover:text-blue-200 transition-colors">Terms of Service</a>
                <a href="#" className="hover:text-blue-200 transition-colors">Help</a>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-3 h-3 bg-green-400 rounded-full animate-pulse shadow-lg"></div>
              <span className="text-xs text-blue-200">System Status: Online</span>
            </div>
          </div>
        </footer>
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