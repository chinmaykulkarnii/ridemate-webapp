import React, { useState, useEffect } from 'react';
import { gamificationService } from '../../services/gamificationService';
import { useToast } from '../../context/ToastContext';
import '../../styles/Gamification.css';

const PointsDisplay = ({ showHistory = false }) => {
  const [totalPoints, setTotalPoints] = useState(0);
  const [pointsHistory, setPointsHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showRedeemModal, setShowRedeemModal] = useState(false);
  const [redeemAmount, setRedeemAmount] = useState('');
  const toast = useToast();

  useEffect(() => {
    loadPointsData();
  }, []);

  const loadPointsData = async () => {
    setLoading(true);
    try {
      const [points, history] = await Promise.all([
        gamificationService.getTotalPoints(),
        showHistory ? gamificationService.getPointsHistory() : Promise.resolve([])
      ]);

      setTotalPoints(points);
      if (showHistory) {
        setPointsHistory(history);
      }
    } catch (error) {
      console.error('Error loading points:', error);
      toast.error('Failed to load points data');
    } finally {
      setLoading(false);
    }
  };

  const handleRedeem = async () => {
    const points = parseInt(redeemAmount);

    if (!points || points <= 0) {
      toast.error('Please enter a valid amount');
      return;
    }

    if (points > totalPoints) {
      toast.error('Insufficient points');
      return;
    }

    if (points < 100) {
      toast.error('Minimum redemption is 100 points');
      return;
    }

    try {
      await gamificationService.redeemPoints(points);
      toast.success(`Redeemed ${points} points successfully!`);
      setShowRedeemModal(false);
      setRedeemAmount('');
      loadPointsData();
    } catch (error) {
      console.error('Error redeeming points:', error);
      toast.error(error.response?.data?.message || 'Failed to redeem points');
    }
  };

  if (loading) {
    return <div className="points-loading">Loading points...</div>;
  }

  return (
    <div className="points-display-container">
      <div className="points-card">
        <div className="points-header">
          <span className="points-icon">🏆</span>
          <div>
            <h3>Your Points</h3>
            <p className="points-subtitle">Earn rewards for every ride</p>
          </div>
        </div>

        <div className="points-total">
          <div className="points-number">{totalPoints}</div>
          <div className="points-label">Total Points</div>
        </div>

        <div className="points-value">
          <span>Discount Value: ₹{Math.floor(totalPoints / 10)}</span>
          <span className="points-rate">(10 points = ₹1)</span>
        </div>

        <button
          className="btn btn-redeem"
          onClick={() => setShowRedeemModal(true)}
          disabled={totalPoints < 100}
        >
          Redeem Points
        </button>

        <div className="points-info">
          <h4>How to earn points:</h4>
          <ul>
            <li>📍 Complete a ride: 50 points</li>
            <li>🚗 Offer a ride: 30 points</li>
            <li>⭐ Receive 5-star rating: 20 points</li>
            <li>👥 Referral signup: 100 points</li>
            <li>✓ Verified account: 50 points</li>
          </ul>
        </div>
      </div>

      {showHistory && pointsHistory.length > 0 && (
        <div className="points-history">
          <h3>Points History</h3>
          <div className="history-list">
            {pointsHistory.map((entry, index) => (
              <div key={index} className="history-item">
                <div className="history-details">
                  <span className="history-description">{entry.description || 'Points earned'}</span>
                  <span className="history-date">
                    {new Date(entry.timestamp || entry.createdAt).toLocaleDateString()}
                  </span>
                </div>
                <div className={`history-points ${entry.points > 0 ? 'positive' : 'negative'}`}>
                  {entry.points > 0 ? '+' : ''}{entry.points}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {showRedeemModal && (
        <div className="modal-overlay" onClick={() => setShowRedeemModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>Redeem Points</h3>
            <p>Convert your points to ride discount</p>

            <div className="redeem-info">
              <div className="redeem-row">
                <span>Available Points:</span>
                <strong>{totalPoints}</strong>
              </div>
              <div className="redeem-row">
                <span>Minimum Redemption:</span>
                <strong>100 points</strong>
              </div>
            </div>

            <div className="form-group">
              <label>Points to Redeem:</label>
              <input
                type="number"
                value={redeemAmount}
                onChange={(e) => setRedeemAmount(e.target.value)}
                placeholder="Enter points"
                min="100"
                max={totalPoints}
                step="10"
              />
              {redeemAmount && (
                <div className="redeem-preview">
                  Discount: ₹{Math.floor(parseInt(redeemAmount) / 10)}
                </div>
              )}
            </div>

            <div className="modal-actions">
              <button
                className="btn btn-cancel"
                onClick={() => setShowRedeemModal(false)}
              >
                Cancel
              </button>
              <button
                className="btn btn-confirm"
                onClick={handleRedeem}
              >
                Redeem
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PointsDisplay;
