// src/App.jsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';

// Layout Components
import Dashboard from './components/layout/Dashboard';

// Auth Pages
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import OTPVerification from './pages/auth/OTPVerification';

// Dashboard Content
import DashboardContent from './pages/Dashboard';

// Import CSS
import './styles/globals.css';

// Protected Route Component
const ProtectedRoute = ({ children, allowedRoles = [] }) => {
  const { user, isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles.length > 0 && !allowedRoles.includes(user?.role)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

// Public Route Component (redirects to dashboard if already logged in)
const PublicRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

// Placeholder components for pages that don't exist yet
const PlaceholderPage = ({ title, description }) => (
  <div className="text-center py-12">
    <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-6">
      <div className="w-8 h-8 bg-blue-600 rounded"></div>
    </div>
    <h1 className="text-2xl font-bold text-gray-900 mb-4">{title}</h1>
    <p className="text-gray-600 mb-8 max-w-md mx-auto">{description}</p>
    <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 max-w-lg mx-auto">
      <p className="text-blue-800 font-medium mb-2">Coming Soon</p>
      <p className="text-blue-600 text-sm">
        This feature is under development and will be available in the next update.
      </p>
    </div>
  </div>
);

// Unauthorized Page Component
const UnauthorizedPage = () => (
  <div className="min-h-screen flex items-center justify-center bg-gray-50">
    <div className="text-center max-w-md mx-auto px-4">
      <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-6">
        <div className="w-8 h-8 bg-red-600 rounded-full"></div>
      </div>
      <h1 className="text-4xl font-bold text-gray-900 mb-4">403</h1>
      <h2 className="text-xl font-semibold text-gray-800 mb-4">Unauthorized Access</h2>
      <p className="text-gray-600 mb-8">You don't have permission to access this page.</p>
      <button
        onClick={() => window.history.back()}
        className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors font-medium"
      >
        Go Back
      </button>
    </div>
  </div>
);

// 404 Page Component
const NotFoundPage = () => (
  <div className="min-h-screen flex items-center justify-center bg-gray-50">
    <div className="text-center max-w-md mx-auto px-4">
      <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
        <div className="w-8 h-8 bg-gray-600 rounded"></div>
      </div>
      <h1 className="text-4xl font-bold text-gray-900 mb-4">404</h1>
      <h2 className="text-xl font-semibold text-gray-800 mb-4">Page Not Found</h2>
      <p className="text-gray-600 mb-8">The page you're looking for doesn't exist.</p>
      <button
        onClick={() => window.location.href = '/dashboard'}
        className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors font-medium"
      >
        Go to Dashboard
      </button>
    </div>
  </div>
);

// Main App Component
function App() {
  return (
    <AuthProvider>
      <NotificationProvider>
        <Router>
          <div className="App">
            <Routes>
              {/* Root redirect */}
              <Route path="/" element={<Navigate to="/dashboard" replace />} />

              {/* Public Routes */}
              <Route
                path="/login"
                element={
                  <PublicRoute>
                    <Login />
                  </PublicRoute>
                }
              />
              <Route
                path="/register"
                element={
                  <PublicRoute>
                    <Register />
                  </PublicRoute>
                }
              />
              <Route
                path="/verify-otp"
                element={
                  <PublicRoute>
                    <OTPVerification />
                  </PublicRoute>
                }
              />

              {/* Protected Routes with Dashboard Layout */}
              <Route
                path="/dashboard"
                element={
                  <ProtectedRoute>
                    <Dashboard>
                      <DashboardContent />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />

              {/* Customer Routes */}
              <Route
                path="/products"
                element={
                  <ProtectedRoute allowedRoles={['CUSTOMER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Browse Products"
                        description="Discover and order from a wide range of water products available in your area."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/products/:productId"
                element={
                  <ProtectedRoute allowedRoles={['CUSTOMER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Product Details"
                        description="View detailed information about the selected water product."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/cart"
                element={
                  <ProtectedRoute allowedRoles={['CUSTOMER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Shopping Cart"
                        description="Review your selected items before placing an order."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/orders"
                element={
                  <ProtectedRoute allowedRoles={['CUSTOMER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Order History"
                        description="Track your current orders and view your order history."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/orders/:orderId/track"
                element={
                  <ProtectedRoute allowedRoles={['CUSTOMER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Order Tracking"
                        description="Track the real-time status of your water delivery."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/addresses"
                element={
                  <ProtectedRoute allowedRoles={['CUSTOMER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Address Management"
                        description="Manage your delivery addresses for quick and easy ordering."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/profile"
                element={
                  <ProtectedRoute allowedRoles={['CUSTOMER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Profile Settings"
                        description="Manage your personal information and account settings."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />

              {/* Business Routes */}
              <Route
                path="/business/dashboard"
                element={
                  <ProtectedRoute allowedRoles={['BUSINESS_OWNER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Business Dashboard"
                        description="Monitor your business performance and manage operations."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/business/profile"
                element={
                  <ProtectedRoute allowedRoles={['BUSINESS_OWNER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Business Profile"
                        description="Manage your business information and settings."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/business/products"
                element={
                  <ProtectedRoute allowedRoles={['BUSINESS_OWNER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Product Management"
                        description="Add, edit, and manage your water products and inventory."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/business/orders"
                element={
                  <ProtectedRoute allowedRoles={['BUSINESS_OWNER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Order Management"
                        description="View and manage customer orders for your business."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/business/analytics"
                element={
                  <ProtectedRoute allowedRoles={['BUSINESS_OWNER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Business Analytics"
                        description="View detailed analytics and performance metrics."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/business/inventory"
                element={
                  <ProtectedRoute allowedRoles={['BUSINESS_OWNER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Inventory Management"
                        description="Track and manage your product inventory levels."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/business/customers"
                element={
                  <ProtectedRoute allowedRoles={['BUSINESS_OWNER']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Customer Management"
                        description="View and manage your customer relationships."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />

              {/* Admin Routes */}
              <Route
                path="/admin/dashboard"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Admin Dashboard"
                        description="Overview of system-wide metrics and management tools."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/users"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="User Management"
                        description="Manage all users, roles, and permissions in the system."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/businesses"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Business Management"
                        description="Verify and manage business accounts and applications."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/analytics"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="System Analytics"
                        description="Comprehensive analytics and reporting for the entire platform."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/settings"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="System Settings"
                        description="Configure system-wide settings and parameters."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/reports"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Reports"
                        description="Generate and view detailed system reports."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/support"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <Dashboard>
                      <PlaceholderPage
                        title="Support Management"
                        description="Manage customer support tickets and inquiries."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />

              {/* Shared Routes (accessible by multiple roles) */}
              <Route
                path="/notifications"
                element={
                  <ProtectedRoute>
                    <Dashboard>
                      <PlaceholderPage
                        title="Notifications"
                        description="View and manage your notifications and alerts."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/help"
                element={
                  <ProtectedRoute>
                    <Dashboard>
                      <PlaceholderPage
                        title="Help & Support"
                        description="Find answers to common questions and contact support."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/settings"
                element={
                  <ProtectedRoute>
                    <Dashboard>
                      <PlaceholderPage
                        title="Account Settings"
                        description="Manage your account preferences and security settings."
                      />
                    </Dashboard>
                  </ProtectedRoute>
                }
              />

              {/* Error Pages */}
              <Route path="/unauthorized" element={<UnauthorizedPage />} />
              <Route path="*" element={<NotFoundPage />} />
            </Routes>
          </div>
        </Router>
      </NotificationProvider>
    </AuthProvider>
  );
}

export default App;