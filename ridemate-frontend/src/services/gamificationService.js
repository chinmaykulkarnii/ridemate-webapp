import api from './api';

export const gamificationService = {
  // Get points history
  getPointsHistory: async () => {
    const response = await api.get('/gamification/points/history');
    return response.data;
  },

  // Get total points
  getTotalPoints: async () => {
    const response = await api.get('/gamification/points/total');
    return response.data;
  },

  // Generate referral code
  generateReferralCode: async () => {
    const response = await api.post('/gamification/referral/generate');
    return response.data;
  },

  // Apply referral code
  applyReferralCode: async (code) => {
    const response = await api.post('/gamification/referral/apply', { code });
    return response.data;
  },

  // Redeem points for discount
  redeemPoints: async (points) => {
    const response = await api.post('/gamification/redeem', { points });
    return response.data;
  },
};
