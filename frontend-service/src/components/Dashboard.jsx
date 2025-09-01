import React, { useState } from 'react';
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
  Truck,
  CreditCard,
  Phone,
  Mail,
  LogOut
} from 'lucide-react';

const WaterwalaApp = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [currentView, setCurrentView] = useState('dashboard');
  const [userRole, setUserRole] = useState('CUSTOMER'); // CUSTOMER, BUSINESS_OWNER, ADMIN

  // Navigation items based on user role
  const getNavItems = () => {
    const baseItems = [
      { id: 'dashboard', label: 'Dashboard', icon: Home }
    ];

    if (userRole === 'CUSTOMER') {
      return [
        ...baseItems,
        { id: 'products', label: 'Browse Products', icon: Package },
        { id: 'orders', label: 'My Orders', icon: ShoppingCart },
        { id: 'addresses', label: 'My Addresses', icon: MapPin },
        { id: 'profile', label: 'Profile', icon: User }
      ];
    }

    if (userRole === 'BUSINESS_OWNER') {
      return [
        ...baseItems,
        { id: 'business-profile', label: 'Business Profile', icon: Store },
        { id: 'products-manage', label: 'Manage Products', icon: Package },
        { id: 'orders-manage', label: 'Manage Orders', icon: ShoppingCart },
        { id: 'analytics', label: 'Analytics', icon: BarChart3 },
        { id: 'profile', label: 'Profile', icon: User }
      ];
    }

    if (userRole === 'ADMIN') {
      return [
        ...baseItems,
        { id: 'users', label: 'User Management', icon: Users },
        { id: 'businesses', label: 'Business Management', icon: Store },
        { id: 'orders-admin', label: 'All Orders', icon: ShoppingCart },
        { id: 'products-admin', label: 'All Products', icon: Package },
        { id: 'analytics-admin', label: 'System Analytics', icon: BarChart3 },
        { id: 'settings', label: 'Settings', icon: Settings }
      ];
    }

    return baseItems;
  };

  const navItems = getNavItems();

  const DashboardContent = () => {
    const stats = [
      { title: 'Total Orders', value: '1,234', icon: ShoppingCart, color: 'bg-blue-500' },
      { title: 'Active Customers', value: '856', icon: Users, color: 'bg-green-500' },
      { title: 'Products Available', value: '45', icon: Package, color: 'bg-purple-500' },
      { title: 'Revenue', value: '₹1,23,456', icon: CreditCard, color: 'bg-orange-500' }
    ];

    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">
            Welcome to Waterwala {userRole === 'ADMIN' ? 'Admin' : userRole === 'BUSINESS_OWNER' ? 'Business' : ''} Dashboard
          </h1>
          <p className="text-gray-600 mt-2">Manage your water delivery operations efficiently</p>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {stats.map((stat, index) => (
            <div key={index} className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition-shadow">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                  <p className="text-2xl font-bold text-gray-900 mt-1">{stat.value}</p>
                </div>
                <div className={`${stat.color} p-3 rounded-lg`}>
                  <stat.icon className="h-6 w-6 text-white" />
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Quick Actions */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Quick Actions</h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {userRole === 'CUSTOMER' && (
              <>
                <button className="flex flex-col items-center p-4 rounded-lg border border-gray-200 hover:bg-blue-50 hover:border-blue-300 transition-colors">
                  <Package className="h-8 w-8 text-blue-600 mb-2" />
                  <span className="text-sm font-medium">Order Water</span>
                </button>
                <button className="flex flex-col items-center p-4 rounded-lg border border-gray-200 hover:bg-green-50 hover:border-green-300 transition-colors">
                  <MapPin className="h-8 w-8 text-green-600 mb-2" />
                  <span className="text-sm font-medium">Add Address</span>
                </button>
              </>
            )}
            {userRole === 'BUSINESS_OWNER' && (
              <>
                <button className="flex flex-col items-center p-4 rounded-lg border border-gray-200 hover:bg-blue-50 hover:border-blue-300 transition-colors">
                  <Package className="h-8 w-8 text-blue-600 mb-2" />
                  <span className="text-sm font-medium">Add Product</span>
                </button>
                <button className="flex flex-col items-center p-4 rounded-lg border border-gray-200 hover:bg-green-50 hover:border-green-300 transition-colors">
                  <ShoppingCart className="h-8 w-8 text-green-600 mb-2" />
                  <span className="text-sm font-medium">View Orders</span>
                </button>
              </>
            )}
            {userRole === 'ADMIN' && (
              <>
                <button className="flex flex-col items-center p-4 rounded-lg border border-gray-200 hover:bg-blue-50 hover:border-blue-300 transition-colors">
                  <Users className="h-8 w-8 text-blue-600 mb-2" />
                  <span className="text-sm font-medium">Manage Users</span>
                </button>
                <button className="flex flex-col items-center p-4 rounded-lg border border-gray-200 hover:bg-green-50 hover:border-green-300 transition-colors">
                  <Store className="h-8 w-8 text-green-600 mb-2" />
                  <span className="text-sm font-medium">Verify Business</span>
                </button>
              </>
            )}
            <button className="flex flex-col items-center p-4 rounded-lg border border-gray-200 hover:bg-purple-50 hover:border-purple-300 transition-colors">
              <BarChart3 className="h-8 w-8 text-purple-600 mb-2" />
              <span className="text-sm font-medium">View Analytics</span>
            </button>
          </div>
        </div>

        {/* Recent Activity */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Recent Activity</h2>
          <div className="space-y-4">
            {[1, 2, 3].map((item) => (
              <div key={item} className="flex items-center space-x-4 p-3 rounded-lg hover:bg-gray-50">
                <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                  <Droplets className="h-5 w-5 text-blue-600" />
                </div>
                <div className="flex-1">
                  <p className="text-sm font-medium text-gray-900">New order #WW-{1000 + item}</p>
                  <p className="text-sm text-gray-500">20L Bisleri Water Jar • 2 mins ago</p>
                </div>
                <span className="text-sm text-green-600 font-medium">₹120</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
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

        {/* User Role Selector (for demo) */}
        <div className="p-4 border-b border-gray-200">
          <select
            value={userRole}
            onChange={(e) => setUserRole(e.target.value)}
            className="w-full p-2 border border-gray-300 rounded-lg text-sm"
          >
            <option value="CUSTOMER">Customer View</option>
            <option value="BUSINESS_OWNER">Business Owner</option>
            <option value="ADMIN">Admin View</option>
          </select>
        </div>

        {/* Navigation */}
        <nav className="p-4 space-y-2">
          {navItems.map((item) => (
            <button
              key={item.id}
              onClick={() => setCurrentView(item.id)}
              className={`w-full flex items-center space-x-3 px-3 py-2 rounded-lg text-left transition-colors ${
                currentView === item.id
                  ? 'bg-blue-50 text-blue-700 border border-blue-200'
                  : 'text-gray-700 hover:bg-gray-100'
              }`}
            >
              <item.icon className="h-5 w-5" />
              <span className="font-medium">{item.label}</span>
            </button>
          ))}
        </nav>

        {/* User Info */}
        <div className="absolute bottom-0 left-0 right-0 p-4 border-t border-gray-200">
          <div className="flex items-center space-x-3 mb-3">
            <div className="w-10 h-10 bg-gray-300 rounded-full flex items-center justify-center">
              <User className="h-5 w-5 text-gray-600" />
            </div>
            <div className="flex-1">
              <p className="text-sm font-medium text-gray-900">John Doe</p>
              <p className="text-xs text-gray-500">{userRole.replace('_', ' ').toLowerCase()}</p>
            </div>
          </div>
          <button className="w-full flex items-center space-x-2 text-red-600 hover:bg-red-50 p-2 rounded-lg transition-colors">
            <LogOut className="h-4 w-4" />
            <span className="text-sm">Logout</span>
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="lg:ml-64">
        {/* Header */}
        <header className="bg-white border-b border-gray-200 px-6 py-4">
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
                  placeholder="Search products, orders, customers..."
                  className="pl-10 pr-4 py-2 w-80 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>

            <div className="flex items-center space-x-4">
              {/* Notifications */}
              <button className="relative p-2 rounded-full hover:bg-gray-100">
                <Bell className="h-5 w-5 text-gray-600" />
                <span className="absolute -top-1 -right-1 h-4 w-4 bg-red-500 rounded-full text-xs text-white flex items-center justify-center">3</span>
              </button>

              {/* Quick Contact */}
              <div className="hidden md:flex items-center space-x-3 text-sm text-gray-600">
                <Phone className="h-4 w-4" />
                <span>+91 98765 43210</span>
              </div>
            </div>
          </div>
        </header>

        {/* Page Content */}
        <main className="p-6">
          {currentView === 'dashboard' && <DashboardContent />}

          {currentView !== 'dashboard' && (
            <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-8 text-center">
              <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <Package className="h-8 w-8 text-blue-600" />
              </div>
              <h2 className="text-2xl font-semibold text-gray-900 mb-2">
                {navItems.find(item => item.id === currentView)?.label} Module
              </h2>
              <p className="text-gray-600 mb-6">
                This module will be implemented next. It will handle all the {currentView.replace('-', ' ')} related operations.
              </p>
              <div className="bg-gray-50 rounded-lg p-4 text-left">
                <h3 className="font-medium text-gray-900 mb-2">Features to implement:</h3>
                <ul className="text-sm text-gray-600 space-y-1">
                  {currentView === 'products' && (
                    <>
                      <li>• Browse and search products</li>
                      <li>• Filter by category, price, brand</li>
                      <li>• Product details and inventory status</li>
                      <li>• Add to cart functionality</li>
                    </>
                  )}
                  {currentView === 'orders' && (
                    <>
                      <li>• View order history with status tracking</li>
                      <li>• Order details and delivery information</li>
                      <li>• Cancel pending orders</li>
                      <li>• Reorder previous purchases</li>
                    </>
                  )}
                  {currentView === 'addresses' && (
                    <>
                      <li>• Add, edit, delete addresses</li>
                      <li>• Set default delivery address</li>
                      <li>• Address validation and geocoding</li>
                    </>
                  )}
                  {currentView.includes('manage') && (
                    <>
                      <li>• Create, update, delete operations</li>
                      <li>• Status management and tracking</li>
                      <li>• Bulk operations and filters</li>
                      <li>• Real-time updates and notifications</li>
                    </>
                  )}
                </ul>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default WaterwalaApp;