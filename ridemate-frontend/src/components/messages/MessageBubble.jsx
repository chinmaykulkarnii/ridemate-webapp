import React from 'react';

const MessageBubble = ({ message, isOwn }) => {
  const formatTime = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className={`message-bubble ${isOwn ? 'own' : 'other'}`}>
      <div className="message-content">{message.content}</div>
      <div className="message-time">{formatTime(message.createdAt)}</div>
    </div>
  );
};

export default MessageBubble;