import React, { useState, useEffect } from 'react';
import { analyticsService } from '../../services/analyticsService';
import { useAuth } from '../../context/AuthContext';
import '../../styles/Gamification.css';

const Leaderboard = () => {
  const [leaderboardData, setLeaderboardData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('points'); // 'points', 'rides', 'ratings'
  const { user } = useAuth();

  useEffect(() => {
    loadLeaderboard();
  }, [filter]);

  const loadLeaderboard = async () => {
    setLoading(true);
    try {
      // Mock data - in production, you'd have a dedicated leaderboard endpoint
      const mockData = [
        { id: 1, name: 'John Doe', points: 1250, rides: 45, rating: 4.9, badge: '🥇' },
        { id: 2, name: 'Jane Smith', points: 1100, rides: 38, rating: 4.8, badge: '🥈' },
        { id: 3, name: 'Mike Johnson', points: 980, rides: 35, rating: 4.7, badge: '🥉' },
        { id: 4, name: 'Sarah Williams', points: 850, rides: 30, rating: 4.6, badge: '' },
        { id: 5, name: 'Tom Brown', points: 720, rides: 28, rating: 4.5, badge: '' },
        { id: user?.id || 6, name: user?.firstName + ' ' + user?.lastName || 'You', points: 650, rides: 25, rating: 4.4, badge: '', isCurrentUser: true },
      ];

      // Sort based on filter
      const sorted = mockData.sort((a, b) => {
        if (filter === 'points') return b.points - a.points;
        if (filter === 'rides') return b.rides - a.rides;
        if (filter === 'ratings') return b.rating - a.rating;
        return 0;
      });

      setLeaderboardData(sorted);
    } catch (error) {
      console.error('Error loading leaderboard:', error);
    } finally {
      setLoading(false);
    }
  };

  const getMedalEmoji = (index) => {
    if (index === 0) return '🥇';
    if (index === 1) return '🥈';
    if (index === 2) return '🥉';
    return '';
  };

  if (loading) {
    return <div className="leaderboard-loading">Loading leaderboard...</div>;
  }

  return (
    <div className="leaderboard-container">
      <div className="leaderboard-header">
        <h2>🏆 Leaderboard</h2>
        <p>Top performers this month</p>
      </div>

      <div className="leaderboard-filters">
        <button
          className={`filter-btn ${filter === 'points' ? 'active' : ''}`}
          onClick={() => setFilter('points')}
        >
          Points
        </button>
        <button
          className={`filter-btn ${filter === 'rides' ? 'active' : ''}`}
          onClick={() => setFilter('rides')}
        >
          Rides
        </button>
        <button
          className={`filter-btn ${filter === 'ratings' ? 'active' : ''}`}
          onClick={() => setFilter('ratings')}
        >
          Ratings
        </button>
      </div>

      <div className="leaderboard-list">
        {leaderboardData.map((entry, index) => (
          <div
            key={entry.id}
            className={`leaderboard-item ${entry.isCurrentUser ? 'current-user' : ''} ${index < 3 ? 'top-three' : ''}`}
          >
            <div className="rank-badge">
              {getMedalEmoji(index) || `#${index + 1}`}
            </div>

            <div className="user-info">
              <div className="user-name">
                {entry.name}
                {entry.isCurrentUser && <span className="you-badge">You</span>}
              </div>
              <div className="user-stats">
                {filter === 'points' && <span>{entry.points} points</span>}
                {filter === 'rides' && <span>{entry.rides} rides</span>}
                {filter === 'ratings' && <span>⭐ {entry.rating}</span>}
              </div>
            </div>

            <div className="score-display">
              {filter === 'points' && <strong>{entry.points}</strong>}
              {filter === 'rides' && <strong>{entry.rides}</strong>}
              {filter === 'ratings' && <strong>{entry.rating}</strong>}
            </div>
          </div>
        ))}
      </div>

      <div className="leaderboard-footer">
        <p>Keep earning points to climb the leaderboard! 🚀</p>
      </div>
    </div>
  );
};

export default Leaderboard;
