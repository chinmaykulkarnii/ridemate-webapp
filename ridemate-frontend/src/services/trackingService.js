import api from './api';

export const trackingService = {
  // Update driver location
  updateLocation: async (rideId, locationData) => {
    const response = await api.post(`/tracking/rides/${rideId}/location`, locationData);
    return response.data;
  },

  // Get current location
  getCurrentLocation: async (rideId) => {
    const response = await api.get(`/tracking/rides/${rideId}/current-location`);
    return response.data;
  },

  // Get location history
  getLocationHistory: async (rideId) => {
    const response = await api.get(`/tracking/rides/${rideId}/location-history`);
    return response.data;
  },

  // Enable tracking
  enableTracking: async (rideId) => {
    const response = await api.post(`/tracking/rides/${rideId}/enable`);
    return response.data;
  },

  // Disable tracking
  disableTracking: async (rideId) => {
    const response = await api.post(`/tracking/rides/${rideId}/disable`);
    return response.data;
  },

  // Get tracking share link
  getShareLink: async (rideId) => {
    const response = await api.get(`/tracking/rides/${rideId}/share-link`);
    return response.data;
  },
};
