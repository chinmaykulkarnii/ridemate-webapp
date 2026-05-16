import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { authService } from '../../services/authService';

const Profile = () => {
  const { user } = useAuth();
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    phoneNumber: user?.phoneNumber || '',
  });
  const [message, setMessage] = useState('');

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await authService.updateProfile(user.id, formData);
      setMessage('Profile updated successfully');
      setEditing(false);
    } catch (error) {
      setMessage('Failed to update profile');
    }
  };

  return (
    <div className="profile-container">
      <div className="profile-card">
        <h2>My Profile</h2>
        {message && <div className="message">{message}</div>}

        <div className="profile-info">
          <div className="profile-avatar">
            <img src={user?.profilePicture || '/default-avatar.png'} alt="Profile" />
          </div>

          <div className="profile-stats">
            <div className="stat">
              <span className="stat-label">Average Rating</span>
              <span className="stat-value">
                {user?.averageRating?.toFixed(1) || 'N/A'} ⭐
              </span>
            </div>
            <div className="stat">
              <span className="stat-label">Total Ratings</span>
              <span className="stat-value">{user?.totalRatings || 0}</span>
            </div>
          </div>
        </div>

        {editing ? (
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>First Name</label>
              <input
                type="text"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label>Last Name</label>
              <input
                type="text"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label>Phone Number</label>
              <input
                type="tel"
                name="phoneNumber"
                value={formData.phoneNumber}
                onChange={handleChange}
                required
              />
            </div>

            <div className="button-group">
              <button type="submit" className="btn btn-primary">Save</button>
              <button type="button" className="btn btn-secondary" onClick={() => setEditing(false)}>
                Cancel
              </button>
            </div>
          </form>
        ) : (
          <>
            <div className="profile-details">
              <p><strong>Email:</strong> {user?.email}</p>
              <p><strong>Name:</strong> {user?.firstName} {user?.lastName}</p>
              <p><strong>Phone:</strong> {user?.phoneNumber}</p>
            </div>
            <button className="btn btn-primary" onClick={() => setEditing(true)}>
              Edit Profile
            </button>
          </>
        )}
      </div>
    </div>
  );
};

export default Profile;