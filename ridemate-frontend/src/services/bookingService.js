import api from './api';

export const bookingService = {
  createBooking: async (bookingData) => {
    const response = await api.post('/bookings', bookingData);
    return response.data;
  },

  getPassengerBookings: async () => {
    const response = await api.get('/bookings/passenger');
    return response.data;
  },

  getDriverBookings: async () => {
    const response = await api.get('/bookings/driver');
    return response.data;
  },

  confirmBooking: async (bookingId) => {
    const response = await api.put(`/bookings/${bookingId}/confirm`);
    return response.data;
  },

  cancelBooking: async (bookingId) => {
    const response = await api.put(`/bookings/${bookingId}/cancel`);
    return response.data;
  },
};