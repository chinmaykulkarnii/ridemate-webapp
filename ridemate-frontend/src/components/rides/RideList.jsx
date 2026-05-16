import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { rideService } from '../../services/rideService';
import RideCard from './RideCard';

const RideList = () => {
  const { user } = useAuth();
  const [rides, setRides] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadRides();
  }, [user]);

  const loadRides = async () => {
    try {
      const data = await rideService.getMyRides(user.id);
      // Ensure data is always an array
      if (Array.isArray(data)) {
        setRides(data);
      } else if (data && Array.isArray(data.rides)) {
        setRides(data.rides);
      } else {
        setRides([]);
      }
    } catch (error) {
      console.error('Failed to load rides:', error);
      setRides([]);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading">Loading...</div>;

  return (
    <div className="ride-list-container">
      <h2>My Posted Rides</h2>
      {rides.length > 0 ? (
        <div className="rides-grid">
          {rides.map(ride => (
            <RideCard key={ride.id} ride={ride} />
          ))}
        </div>
      ) : (
        <div className="empty-state">
          <p>You haven't posted any rides yet.</p>
          <a href="/rides/create" className="btn btn-primary">Post a Ride</a>
        </div>
      )}
    </div>
  );
};

export default RideList;