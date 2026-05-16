import api from './api';

export const messageService = {
  getConversations: async () => {
    const response = await api.get('/messages/conversations');
    return response.data;
  },

  getConversation: async (userId) => {
    const response = await api.get(`/messages/conversation/${userId}`);
    return response.data;
  },

  getUnreadMessages: async () => {
    const response = await api.get('/messages/unread');
    return response.data;
  },

  markAsRead: async (messageId) => {
    const response = await api.put(`/messages/${messageId}/read`);
    return response.data;
  },
};