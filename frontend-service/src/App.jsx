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

// Customer Pages
import ProductBrowse from './pages/customer/ProductBrowse';
import ProductDetail from './pages/customer/ProductDetail';
import Cart from './pages/customer/Cart';
import OrderHistory from './pages/customer/OrderHistory';
import OrderTracking from './pages/customer/OrderTracking';
import AddressManagement from './pages/customer/AddressManagement';

// Business Pages
import BusinessDashboard from './pages/business/BusinessDashboard';
import ProductManagement from './pages/business/ProductManagement';
import OrderManagement from './pages/business/OrderManagement';
import BusinessProfile from './pages/business/BusinessProfile';
import BusinessAnalytics from './pages/business/Analytics';

// Admin Pages
import AdminDashboard from './pages/admin/AdminDashboard';
import UserManagement from './pages/admin/UserManagement';
import BusinessVerification from './pages/admin/BusinessVerification';
import SystemAnalytics from './pages/admin/SystemAnalytics';

// Import CSS
import './styles/globals.css';

// Protected Route Component
const ProtectedRoute = ({ children, allowedRoles = [] }) => {
  const { user, isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
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
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

// Unauthorized Page Component
const UnauthorizedPage = () => (
  <div className="min-h-screen flex items-center justify-center bg-gray-50">
    <div className="text-center">
      <h1 className="text-4xl font-bold text-gray-900 mb-4">403</h1>
      <p className="text-xl text-gray-600 mb-8">Unauthorized Access</p>
      <p className="text-gray-500 mb-8">You don't have permission to access this page.</p>
      <button
        onClick={() => window.history.back()}
        className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors"
      >
        Go Back
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

              {/* Protected Routes */}
              <Route
                path="/dashboard"
                element={
                  <ProtectedRoute>
                    <Dashboard />
                  </ProtectedRoute>
                }
              />

              {/* Customer Routes */}
              <Route
                path="/products"
                element={
                  <ProtectedRoute allowedRoles={['CUSTOMER']}>
                    <ProductBrowse />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/products/:productId"
                element={
                  <ProtectedRoute allowedRoles={['CUSTOMER']}>
                    <ProductDetail />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/cart"
                element={
                  <ProtectedRoute allowedRoles={['CUSTOMER']}>
                    <Cart />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/orders"
                element={
                  <ProtectedRoute allowedRoles={['CUSTOMER']}>
                    <OrderHistory />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/orders/:orderId/track"
                element={
                  <ProtectedRoute allowedRoles={['CUSTOMER']}>
                    <OrderTracking />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/addresses"
                element={
                  <ProtectedRoute allowedRoles={['CUSTOMER']}>
                    <AddressManagement />
                  </ProtectedRoute>
                }
              />

              {/* Business Routes */}
              <Route
                path="/business/dashboard"
                element={
                  <ProtectedRoute allowedRoles={['BUSINESS_OWNER']}>
                    <BusinessDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/business/products"
                element={
                  <ProtectedRoute allowedRoles={['BUSINESS_OWNER']}>
                    <ProductManagement />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/business/orders"
                element={
                  <ProtectedRoute allowedRoles={['BUSINESS_OWNER']}>
                    <OrderManagement />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/business/profile"
                element={
                  <ProtectedRoute allowedRoles={['BUSINESS_OWNER']}>
                    <BusinessProfile />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/business/analytics"
                element={
                  <ProtectedRoute allowedRoles={['BUSINESS_OWNER']}>
                    <BusinessAnalytics />
                  </ProtectedRoute>
                }
              />

              {/* Admin Routes */}
              <Route
                path="/admin/dashboard"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <AdminDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/users"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <UserManagement />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/businesses"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <BusinessVerification />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/analytics"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <SystemAnalytics />
                  </ProtectedRoute>
                }
              />

              {/* Utility Routes */}
              <Route path="/unauthorized" element={<UnauthorizedPage />} />
              <Route path="/" element={<Navigate to="/dashboard" replace />} />

              {/* 404 Route */}
              <Route
                path="*"
                element={
                  <div className="min-h-screen flex items-center justify-center bg-gray-50">
                    <div className="text-center">
                      <h1 className="text-4xl font-bold text-gray-900 mb-4">404</h1>
                      <p className="text-xl text-gray-600 mb-8">Page Not Found</p>
                      <button
                        onClick={() => window.location.href = '/dashboard'}
                        className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors"
                      >
                        Go to Dashboard
                      </button>
                    </div>
                  </div>
                }
              />
            </Routes>
          </div>
        </Router>
      </NotificationProvider>
    </AuthProvider>
  );
}

export default App;