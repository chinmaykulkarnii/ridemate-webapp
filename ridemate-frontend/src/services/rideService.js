import api from './api';

export const rideService = {
  createRide: async (rideData) => {
    const response = await api.post('/rides', rideData);
    return response.data;
  },

  getRideById: async (id) => {
    const response = await api.get(`/rides/${id}`);
    return response.data;
  },

  searchRides: async (filters) => {
    const params = new URLSearchParams(filters);
    const response = await api.get(`/rides/search?${params}`);
    return response.data;
  },

  getMyRides: async (userId) => {
    const response = await api.get(`/rides/driver/${userId}`);
    return response.data;
  },

  updateRide: async (id, rideData) => {
    const response = await api.put(`/rides/${id}`, rideData);
    return response.data;
  },

  deleteRide: async (id) => {
    const response = await api.delete(`/rides/${id}`);
    return response.data;
  },
};
