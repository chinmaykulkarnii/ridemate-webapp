import api from './api';

export const analyticsService = {
  // Get current user analytics
  getMyAnalytics: async () => {
    const response = await api.get('/analytics/user');
    return response.data;
  },

  // Get specific user analytics (admin)
  getUserAnalytics: async (userId) => {
    const response = await api.get(`/analytics/user/${userId}`);
    return response.data;
  },

  // Get ride analytics
  getRideAnalytics: async (rideId) => {
    const response = await api.get(`/analytics/ride/${rideId}`);
    return response.data;
  },

  // Get admin dashboard metrics
  getAdminDashboard: async () => {
    const response = await api.get('/analytics/admin/dashboard');
    return response.data;
  },
};
