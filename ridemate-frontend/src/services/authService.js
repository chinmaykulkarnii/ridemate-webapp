import api from './api';

export const authService = {
  // Login user using email and password (session-based)
  login: async (email, password) => {
    const response = await api.post('/auth/login', { email, password });
    return response.data;
  },

  // Register a new user
  signup: async (userData) => {
    const response = await api.post('/auth/signup', userData);
    return response.data;
  },

  // Logout user (clear session)
  logout: async () => {
    const response = await api.post('/auth/logout');
    return response.data;
  },

  // Fetch currently authenticated user's info from session
  getCurrentUser: async () => {
    const response = await api.get('/users/me');
    return response.data;
  },

  // Update user profile
  updateProfile: async (userId, userData) => {
    const response = await api.put(`/users/${userId}`, userData);
    return response.data;
  },
};