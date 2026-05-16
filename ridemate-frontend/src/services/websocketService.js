import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const WS_URL = process.env.REACT_APP_WS_URL || 'http://localhost:8080/ws';

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
  }

  connect() {
    const socket = new SockJS(WS_URL);
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect({}, () => {
      this.connected = true;
      console.log('WebSocket connected');
    }, (error) => {
      console.error('WebSocket connection error:', error);
      this.connected = false;
    });
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect();
      this.connected = false;
    }
  }

  subscribeToNotifications(userId, callback) {
    if (this.stompClient && this.connected) {
      this.stompClient.subscribe(`/user/${userId}/queue/notifications`, (message) => {
        const notification = JSON.parse(message.body);
        callback(notification);
      });
    }
  }

  subscribeToMessages(userId, callback) {
    if (this.stompClient && this.connected) {
      this.stompClient.subscribe(`/user/${userId}/queue/messages`, (message) => {
        const msg = JSON.parse(message.body);
        callback(msg);
      });
    }
  }

  sendMessage(senderId, receiverId, content) {
    if (this.stompClient && this.connected) {
      this.stompClient.send('/app/chat.send', {}, JSON.stringify({
        senderId,
        receiverId,
        content
      }));
    }
  }
}

export const websocketService = new WebSocketService();