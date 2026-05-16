import React, { useState, useEffect } from 'react';
import { messageService } from '../../services/messageService';
import ChatWindow from './ChatWindow';

const ChatList = () => {
  const [conversations, setConversations] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    messageService.getConversations()
      .then(setConversations)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  // Re-fetch list when user closes a chat window so unread counts update
  const handleClose = () => {
    setSelectedUser(null);
    messageService.getConversations().then(setConversations).catch(console.error);
  };

  const formatTime = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const now = new Date();
    const isToday = date.toDateString() === now.toDateString();
    return isToday
      ? date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      : date.toLocaleDateString([], { month: 'short', day: 'numeric' });
  };

  return (
    <div className="chat-container">
      <div className="conversations-panel">
        <h2>Messages</h2>

        {loading && <p className="loading-text">Loading conversations...</p>}

        {!loading && conversations.length === 0 && (
          <div className="empty-state">
            <p>No conversations yet.</p>
            <p>Start chatting from your bookings!</p>
          </div>
        )}

        {conversations.map(conv => (
          <div
            key={conv.otherUserId}
            className={`conversation-item ${selectedUser?.id === conv.otherUserId ? 'active' : ''}`}
            onClick={() => setSelectedUser({
              id: conv.otherUserId,
              firstName: conv.firstName,
              lastName: conv.lastName,
              profilePicture: conv.profilePicture,
            })}
          >
            <img
              src={conv.profilePicture || '/default-avatar.png'}
              alt={conv.firstName}
              className="avatar"
            />
            <div className="conv-info">
              <div className="conv-header">
                <span className="conv-name">{conv.firstName} {conv.lastName}</span>
                <span className="conv-time">{formatTime(conv.lastMessageTime)}</span>
              </div>
              <div className="conv-footer">
                <span className="conv-last-message">{conv.lastMessage}</span>
                {conv.unreadCount > 0 && (
                  <span className="unread-badge">{conv.unreadCount}</span>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="chat-panel">
        {selectedUser ? (
          <ChatWindow otherUser={selectedUser} onClose={handleClose} />
        ) : (
          <div className="no-chat-selected">
            <p>Select a conversation to start chatting</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ChatList;
