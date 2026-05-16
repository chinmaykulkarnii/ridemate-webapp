import api from './api';

export const emergencyService = {
  // Trigger SOS alert
  triggerSOS: async (sosData) => {
    const response = await api.post('/emergency/sos', sosData);
    return response.data;
  },

  // Resolve SOS
  resolveSOS: async (sosId) => {
    const response = await api.post(`/emergency/${sosId}/resolve`);
    return response.data;
  },

  // Mark SOS as false alarm
  markFalseAlarm: async (sosId) => {
    const response = await api.post(`/emergency/${sosId}/false-alarm`);
    return response.data;
  },

  // Get active SOS alerts
  getActiveSOS: async () => {
    const response = await api.get('/emergency/active');
    return response.data;
  },

  // Get SOS history
  getSOSHistory: async () => {
    const response = await api.get('/emergency/history');
    return response.data;
  },

  // Get SOS details
  getSOSById: async (sosId) => {
    const response = await api.get(`/emergency/${sosId}`);
    return response.data;
  },
};
