import React, { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Check if user is logged in via session
  useEffect(() => {
    authService.getCurrentUser()
      .then(userData => {
        setUser({
          id: userData.id,
          email: userData.email,
          firstName: userData.firstName,
          lastName: userData.lastName
        });
      })
      .catch(() => {
        // Not logged in
        setUser(null);
      })
      .finally(() => setLoading(false));
  }, []);

  // Handle OAuth redirect
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    if (window.location.pathname === '/oauth2/redirect') {
      const userId = params.get('userId');
      const email = params.get('email');
      const firstName = params.get('firstName');
      const lastName = params.get('lastName');

      if (userId && email) {
        setUser({ id: userId, email, firstName, lastName });
        window.history.replaceState({}, document.title, '/search');
      }
    }
  }, []);

  // Local login with email/password
  const login = async (email, password) => {
    const response = await authService.login(email, password);
    const userInfo = {
      id: response.id,
      email: response.email,
      firstName: response.firstName,
      lastName: response.lastName
    };
    setUser(userInfo);
    return response;
  };

  // OAuth login - redirect to backend OAuth endpoint
  const loginWithOAuth = (provider) => {
    window.location.href = `http://localhost:8080/oauth2/authorization/${provider}`;
  };

  // Signup function
  const signup = async (userData) => {
    await authService.signup(userData);
  };

  // Logout
  const logout = async () => {
    try {
      await authService.logout();
    } catch (error) {
      console.error('Logout error:', error);
    }
    setUser(null);
  };

  const value = {
    user,
    login,
    loginWithOAuth,
    signup,
    logout,
    isAuthenticated: !!user,
    loading
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};