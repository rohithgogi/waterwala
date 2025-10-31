import React from 'react';
import { LoginForm } from '../components/auth/LoginForm';

export default function Login() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-50 flex items-center justify-center p-4">
      <LoginForm />
    </div>
  );
}