import api from './api';

export const paymentService = {
  // Verify Razorpay payment
  verifyPayment: async (paymentData) => {
    const response = await api.post('/payments/verify', paymentData);
    return response.data;
  },

  // Complete cash payment
  completeCashPayment: async (paymentId) => {
    const response = await api.post(`/payments/${paymentId}/complete-cash`);
    return response.data;
  },

  // Request refund
  requestRefund: async (paymentId, reason) => {
    const response = await api.post(`/payments/${paymentId}/refund`, { reason });
    return response.data;
  },

  // Get user's payment history
  getMyPayments: async () => {
    const response = await api.get('/payments/my-payments');
    return response.data;
  },

  // Get payments for a specific booking
  getBookingPayments: async (bookingId) => {
    const response = await api.get(`/payments/booking/${bookingId}`);
    return response.data;
  },

  // Get payment details
  getPaymentById: async (paymentId) => {
    const response = await api.get(`/payments/${paymentId}`);
    return response.data;
  },
};
