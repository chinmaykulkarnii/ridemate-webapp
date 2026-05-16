import api from './api';

export const locationService = {
  // Geocode address to coordinates
  geocode: async (address) => {
    const response = await api.get('/location/geocode', {
      params: { address }
    });
    return response.data;
  },

  // Reverse geocode coordinates to address
  reverseGeocode: async (latitude, longitude) => {
    const response = await api.get('/location/reverse-geocode', {
      params: { latitude, longitude }
    });
    return response.data;
  },

  // Get route information (distance and duration)
  getRoute: async (startLat, startLon, endLat, endLon) => {
    const response = await api.get('/location/route', {
      params: {
        startLat,
        startLon,
        endLat,
        endLon
      }
    });
    return response.data;
  },

  // Search/autocomplete locations
  searchLocations: async (query) => {
    const response = await api.get('/location/search', {
      params: { query }
    });
    return response.data;
  },

  // Calculate distance between two points
  calculateDistance: async (lat1, lon1, lat2, lon2) => {
    const response = await api.get('/location/distance', {
      params: { lat1, lon1, lat2, lon2 }
    });
    return response.data;
  },
};
