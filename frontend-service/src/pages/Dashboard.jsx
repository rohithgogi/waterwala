// src/pages/Dashboard.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  ShoppingCart,
  Users,
  Package,
  CreditCard,
  Droplets,
  TrendingUp,
  Clock,
  MapPin,
  Plus,
  ArrowRight,
  Bell,
  Calendar
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useNotification } from '../context/NotificationContext';

const DashboardContent = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { showInfo } = useNotification();

  // Sample data - replace with real API calls
  const [dashboardData, setDashboardData] = useState({
    stats: {
      totalOrders: 0,
      activeCustomers: 0,
      productsAvailable: 0,
      revenue: 0
    },
    recentActivity: [],
    quickStats: []
  });

  useEffect(() => {
    // Simulate loading dashboard data
    loadDashboardData();
  }, [user]);

  const loadDashboardData = () => {
    // Mock data based on user role
    if (user?.role === 'CUSTOMER') {
      setDashboardData({
        stats: {
          totalOrders: 12,
          pendingOrders: 2,
          totalSpent: 2450,
          savedAddresses: 3
        },
        recentActivity: [
          { id: 1, type: 'order', message: 'Order #WW-1001 delivered successfully', time: '2 hours ago', status: 'completed' },
          { id: 2, type: 'order', message: 'Order #WW-1002 is out for delivery', time: '1 day ago', status: 'shipping' },
          { id: 3, type: 'order', message: 'Order #WW-1003 confirmed', time: '3 days ago', status: 'confirmed' }
        ]
      });
    } else if (user?.role === 'BUSINESS_OWNER') {
      setDashboardData({
        stats: {
          totalOrders: 156,
          pendingOrders: 12,
          totalRevenue: 45600,
          activeProducts: 8
        },
        recentActivity: [
          { id: 1, type: 'order', message: 'New order #WW-2001 received', time: '30 mins ago', status: 'new' },
          { id: 2, type: 'order', message: 'Order #WW-2002 marked as delivered', time: '2 hours ago', status: 'completed' },
          { id: 3, type: 'product', message: 'Product "20L Bisleri" stock updated', time: '1 day ago', status: 'updated' }
        ]
      });
    } else {
      setDashboardData({
        stats: {
          totalUsers: 1234,
          totalBusinesses: 56,
          totalOrders: 2890,
          systemRevenue: 125000
        },
        recentActivity: [
          { id: 1, type: 'user', message: 'New business registration pending approval', time: '1 hour ago', status: 'pending' },
          { id: 2, type: 'order', message: 'Daily order volume reached 500+', time: '3 hours ago', status: 'milestone' },
          { id: 3, type: 'system', message: 'System maintenance completed', time: '1 day ago', status: 'completed' }
        ]
      });
    }
  };

  const getStatsForRole = () => {
    if (user?.role === 'CUSTOMER') {
      return [
        { title: 'Total Orders', value: dashboardData.stats.totalOrders, icon: ShoppingCart, color: 'bg-blue-500', trend: '+12%' },
        { title: 'Pending Orders', value: dashboardData.stats.pendingOrders, icon: Clock, color: 'bg-orange-500', trend: '+2' },
        { title: 'Total Spent', value: `₹${dashboardData.stats.totalSpent}`, icon: CreditCard, color: 'bg-green-500', trend: '+8%' },
        { title: 'Saved Addresses', value: dashboardData.stats.savedAddresses, icon: MapPin, color: 'bg-purple-500', trend: '+1' }
      ];
    } else if (user?.role === 'BUSINESS_OWNER') {
      return [
        { title: 'Total Orders', value: dashboardData.stats.totalOrders, icon: ShoppingCart, color: 'bg-blue-500', trend: '+23%' },
        { title: 'Pending Orders', value: dashboardData.stats.pendingOrders, icon: Clock, color: 'bg-orange-500', trend: '+5' },
        { title: 'Revenue', value: `₹${dashboardData.stats.totalRevenue?.toLocaleString()}`, icon: CreditCard, color: 'bg-green-500', trend: '+15%' },
        { title: 'Active Products', value: dashboardData.stats.activeProducts, icon: Package, color: 'bg-purple-500', trend: '+2' }
      ];
    } else {
      return [
        { title: 'Total Users', value: dashboardData.stats.totalUsers?.toLocaleString(), icon: Users, color: 'bg-blue-500', trend: '+12%' },
        { title: 'Businesses', value: dashboardData.stats.totalBusinesses, icon: Package, color: 'bg-green-500', trend: '+8%' },
        { title: 'Total Orders', value: dashboardData.stats.totalOrders?.toLocaleString(), icon: ShoppingCart, color: 'bg-orange-500', trend: '+25%' },
        { title: 'System Revenue', value: `₹${dashboardData.stats.systemRevenue?.toLocaleString()}`, icon: CreditCard, color: 'bg-purple-500', trend: '+18%' }
      ];
    }
  };

  const getQuickActionsForRole = () => {
    if (user?.role === 'CUSTOMER') {
      return [
        { label: 'Order Water', icon: Package, color: 'blue', action: () => navigate('/products') },
        { label: 'Track Order', icon: MapPin, color: 'green', action: () => navigate('/orders') },
        { label: 'Add Address', icon: MapPin, color: 'purple', action: () => navigate('/addresses') },
        { label: 'View Profile', icon: Users, color: 'gray', action: () => navigate('/profile') }
      ];
    } else if (user?.role === 'BUSINESS_OWNER') {
      return [
        { label: 'Add Product', icon: Package, color: 'blue', action: () => navigate('/business/products') },
        { label: 'View Orders', icon: ShoppingCart, color: 'green', action: () => navigate('/business/orders') },
        { label: 'Analytics', icon: TrendingUp, color: 'purple', action: () => navigate('/business/analytics') },
        { label: 'Profile', icon: Users, color: 'gray', action: () => navigate('/business/profile') }
      ];
    } else {
      return [
        { label: 'Manage Users', icon: Users, color: 'blue', action: () => navigate('/admin/users') },
        { label: 'Verify Business', icon: Package, color: 'green', action: () => navigate('/admin/businesses') },
        { label: 'System Analytics', icon: TrendingUp, color: 'purple', action: () => navigate('/admin/analytics') },
        { label: 'Settings', icon: CreditCard, color: 'gray', action: () => navigate('/admin/settings') }
      ];
    }
  };

  const handleQuickAction = (action) => {
    if (action) {
      action();
    } else {
      showInfo('Feature Coming Soon', 'This feature will be available in the next update');
    }
  };

  const getWelcomeMessage = () => {
    const hour = new Date().getHours();
    let greeting = 'Good morning';
    if (hour >= 12 && hour < 17) greeting = 'Good afternoon';
    else if (hour >= 17) greeting = 'Good evening';

    const roleText = user?.role === 'CUSTOMER' ? '' :
                    user?.role === 'BUSINESS_OWNER' ? ' Business' : ' Admin';

    return `${greeting}, ${user?.firstName}! Welcome to your Waterwala${roleText} Dashboard`;
  };

  const stats = getStatsForRole();
  const quickActions = getQuickActionsForRole();

  return (
    <div className="space-y-6">
      {/* Welcome Section */}
      <div className="bg-gradient-to-r from-blue-500 to-blue-600 rounded-2xl text-white p-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold mb-2">{getWelcomeMessage()}</h1>
            <p className="text-blue-100">
              {user?.role === 'CUSTOMER' && "Manage your water orders and deliveries"}
              {user?.role === 'BUSINESS_OWNER' && "Manage your business operations efficiently"}
              {user?.role === 'ADMIN' && "Monitor and manage the entire platform"}
            </p>
          </div>
          <div className="hidden md:block">
            <div className="w-20 h-20 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
              <Droplets className="h-10 w-10 text-white" />
            </div>
          </div>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => (
          <div key={index} className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition-shadow">
            <div className="flex items-center justify-between mb-4">
              <div className={`${stat.color} p-3 rounded-lg`}>
                <stat.icon className="h-6 w-6 text-white" />
              </div>
              {stat.trend && (
                <div className="flex items-center text-sm text-green-600">
                  <TrendingUp className="h-4 w-4 mr-1" />
                  <span>{stat.trend}</span>
                </div>
              )}
            </div>
            <div>
              <p className="text-sm font-medium text-gray-600 mb-1">{stat.title}</p>
              <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
            </div>
          </div>
        ))}
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h2 className="text-xl font-semibold text-gray-900 mb-6">Quick Actions</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {quickActions.map((action, index) => (
            <button
              key={index}
              onClick={() => handleQuickAction(action.action)}
              className={`flex flex-col items-center p-4 rounded-lg border border-gray-200 hover:border-${action.color}-300 hover:bg-${action.color}-50 transition-colors group`}
            >
              <div className={`w-12 h-12 bg-${action.color}-100 group-hover:bg-${action.color}-200 rounded-lg flex items-center justify-center mb-3 transition-colors`}>
                <action.icon className={`h-6 w-6 text-${action.color}-600`} />
              </div>
              <span className="text-sm font-medium text-gray-900 text-center">{action.label}</span>
            </button>
          ))}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Activity */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-semibold text-gray-900">Recent Activity</h2>
            <button className="text-blue-600 hover:text-blue-700 text-sm font-medium">
              View All
            </button>
          </div>
          <div className="space-y-4">
            {dashboardData.recentActivity.length > 0 ? (
              dashboardData.recentActivity.map((activity) => (
                <div key={activity.id} className="flex items-start space-x-4 p-3 rounded-lg hover:bg-gray-50 transition-colors">
                  <div className={`w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 ${
                    activity.status === 'completed' ? 'bg-green-100' :
                    activity.status === 'pending' ? 'bg-yellow-100' :
                    activity.status === 'new' ? 'bg-blue-100' :
                    'bg-gray-100'
                  }`}>
                    {activity.type === 'order' ?
                      <ShoppingCart className={`h-5 w-5 ${
                        activity.status === 'completed' ? 'text-green-600' :
                        activity.status === 'pending' ? 'text-yellow-600' :
                        activity.status === 'new' ? 'text-blue-600' :
                        'text-gray-600'
                      }`} /> :
                      <Bell className={`h-5 w-5 ${
                        activity.status === 'completed' ? 'text-green-600' :
                        activity.status === 'pending' ? 'text-yellow-600' :
                        'text-blue-600'
                      }`} />
                    }
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900 mb-1">{activity.message}</p>
                    <p className="text-xs text-gray-500">{activity.time}</p>
                  </div>
                </div>
              ))
            ) : (
              <div className="text-center py-8 text-gray-500">
                <Bell className="h-12 w-12 text-gray-300 mx-auto mb-4" />
                <p>No recent activity</p>
              </div>
            )}
          </div>
        </div>

        {/* Quick Stats or Tips */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">
            {user?.role === 'CUSTOMER' ? 'Tips & Recommendations' : 'Performance Insights'}
          </h2>
          <div className="space-y-4">
            {user?.role === 'CUSTOMER' ? (
              <>
                <div className="flex items-start space-x-3 p-3 bg-blue-50 rounded-lg">
                  <Droplets className="h-5 w-5 text-blue-600 mt-0.5" />
                  <div>
                    <p className="text-sm font-medium text-blue-900">Stay Hydrated</p>
                    <p className="text-xs text-blue-700">Drink at least 8 glasses of water daily for better health</p>
                  </div>
                </div>
                <div className="flex items-start space-x-3 p-3 bg-green-50 rounded-lg">
                  <Calendar className="h-5 w-5 text-green-600 mt-0.5" />
                  <div>
                    <p className="text-sm font-medium text-green-900">Schedule Orders</p>
                    <p className="text-xs text-green-700">Set up recurring orders to never run out of water</p>
                  </div>
                </div>
                <div className="flex items-start space-x-3 p-3 bg-purple-50 rounded-lg">
                  <CreditCard className="h-5 w-5 text-purple-600 mt-0.5" />
                  <div>
                    <p className="text-sm font-medium text-purple-900">Save Money</p>
                    <p className="text-xs text-purple-700">Bulk orders get better discounts and free delivery</p>
                  </div>
                </div>
              </>
            ) : (
              <>
                <div className="flex items-center justify-between p-3 bg-blue-50 rounded-lg">
                  <div>
                    <p className="text-sm font-medium text-blue-900">Order Completion Rate</p>
                    <p className="text-xs text-blue-700">Current week performance</p>
                  </div>
                  <span className="text-lg font-bold text-blue-600">96%</span>
                </div>
                <div className="flex items-center justify-between p-3 bg-green-50 rounded-lg">
                  <div>
                    <p className="text-sm font-medium text-green-900">Customer Satisfaction</p>
                    <p className="text-xs text-green-700">Average rating this month</p>
                  </div>
                  <span className="text-lg font-bold text-green-600">4.8⭐</span>
                </div>
                <div className="flex items-center justify-between p-3 bg-orange-50 rounded-lg">
                  <div>
                    <p className="text-sm font-medium text-orange-900">Delivery Time</p>
                    <p className="text-xs text-orange-700">Average delivery duration</p>
                  </div>
                  <span className="text-lg font-bold text-orange-600">2.5h</span>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardContent;