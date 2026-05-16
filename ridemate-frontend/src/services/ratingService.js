import api from './api';

export const ratingService = {
  createRatings: async (ratingData) => {
    const response = await api.post('/ratings', ratingData);
    return response.data;
  },

  getUserRatings: async (userId) => {
    const response = await api.get(`/ratings/user/${userId}`);
    return response.data;
  },

  getBookingRatings: async (bookingId) => {
    const response = await api.get(`/ratings/booking/${bookingId}`);
    return response.data;
  },
};