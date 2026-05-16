import React, { useState, useEffect } from 'react';
import { gamificationService } from '../../services/gamificationService';
import { useToast } from '../../context/ToastContext';
import { useAuth } from '../../context/AuthContext';
import '../../styles/Gamification.css';

const ReferralProgram = () => {
  const [referralCode, setReferralCode] = useState('');
  const [loading, setLoading] = useState(true);
  const [applyCode, setApplyCode] = useState('');
  const toast = useToast();
  const { user } = useAuth();

  useEffect(() => {
    loadReferralCode();
  }, []);

  const loadReferralCode = async () => {
    setLoading(true);
    try {
      // Check if user already has a referral code
      if (user?.referralCode) {
        setReferralCode(user.referralCode);
      } else {
        // Generate new one
        const code = await gamificationService.generateReferralCode();
        setReferralCode(code);
      }
    } catch (error) {
      console.error('Error loading referral code:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCopyCode = () => {
    navigator.clipboard.writeText(referralCode);
    toast.success('Referral code copied to clipboard!');
  };

  const handleShare = async () => {
    const shareText = `Join RideMate and get 100 bonus points! Use my referral code: ${referralCode}`;
    const shareUrl = `${window.location.origin}/signup?ref=${referralCode}`;

    if (navigator.share) {
      try {
        await navigator.share({
          title: 'Join RideMate',
          text: shareText,
          url: shareUrl,
        });
        toast.success('Thanks for sharing!');
      } catch (error) {
        if (error.name !== 'AbortError') {
          console.error('Error sharing:', error);
        }
      }
    } else {
      // Fallback: copy link
      navigator.clipboard.writeText(`${shareText}\n${shareUrl}`);
      toast.success('Share link copied to clipboard!');
    }
  };

  const handleApplyReferral = async () => {
    if (!applyCode.trim()) {
      toast.error('Please enter a referral code');
      return;
    }

    try {
      await gamificationService.applyReferralCode(applyCode);
      toast.success('Referral code applied! You earned 100 bonus points!');
      setApplyCode('');
    } catch (error) {
      console.error('Error applying referral:', error);
      toast.error(error.response?.data?.message || 'Invalid or already used referral code');
    }
  };

  if (loading) {
    return <div className="referral-loading">Loading...</div>;
  }

  return (
    <div className="referral-container">
      <div className="referral-card">
        <div className="referral-header">
          <span className="referral-icon">🎁</span>
          <h2>Refer & Earn</h2>
          <p>Invite friends and earn rewards together!</p>
        </div>

        <div className="referral-code-section">
          <h3>Your Referral Code</h3>
          <div className="referral-code-display">
            <code className="referral-code">{referralCode}</code>
            <button className="btn-copy" onClick={handleCopyCode} title="Copy code">
              📋
            </button>
          </div>

          <button className="btn btn-share" onClick={handleShare}>
            Share Referral Link
          </button>
        </div>

        <div className="referral-benefits">
          <h3>Referral Benefits</h3>
          <div className="benefits-grid">
            <div className="benefit-item">
              <span className="benefit-icon">👥</span>
              <div>
                <strong>You Get</strong>
                <p>100 points per referral</p>
              </div>
            </div>
            <div className="benefit-item">
              <span className="benefit-icon">🎉</span>
              <div>
                <strong>Friend Gets</strong>
                <p>100 welcome points</p>
              </div>
            </div>
            <div className="benefit-item">
              <span className="benefit-icon">💰</span>
              <div>
                <strong>Both Get</strong>
                <p>₹50 ride discount</p>
              </div>
            </div>
          </div>
        </div>

        <div className="referral-stats">
          <div className="stat-item">
            <div className="stat-number">0</div>
            <div className="stat-label">Friends Referred</div>
          </div>
          <div className="stat-item">
            <div className="stat-number">0</div>
            <div className="stat-label">Points Earned</div>
          </div>
        </div>
      </div>

      <div className="apply-referral-card">
        <h3>Have a Referral Code?</h3>
        <p>Apply a friend's code to get bonus points!</p>

        <div className="apply-referral-form">
          <input
            type="text"
            value={applyCode}
            onChange={(e) => setApplyCode(e.target.value.toUpperCase())}
            placeholder="Enter referral code"
            className="referral-input"
          />
          <button
            className="btn btn-apply"
            onClick={handleApplyReferral}
            disabled={!applyCode.trim()}
          >
            Apply Code
          </button>
        </div>
      </div>

      <div className="referral-how-it-works">
        <h3>How It Works</h3>
        <ol>
          <li>Share your unique referral code with friends</li>
          <li>They sign up using your code</li>
          <li>Both of you receive 100 bonus points</li>
          <li>Redeem points for ride discounts</li>
        </ol>
      </div>
    </div>
  );
};

export default ReferralProgram;
