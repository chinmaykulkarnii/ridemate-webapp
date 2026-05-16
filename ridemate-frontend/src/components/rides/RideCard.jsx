import React from 'react';
import { Link } from 'react-router-dom';

const RideCard = ({ ride }) => {
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  return (
    <div className="ride-card">
      <div className="ride-header">
        <span className="vehicle-type">{ride.vehicleType}</span>
        <span className="price">₹{ride.pricePerSeat}/seat</span>
      </div>

      <div className="ride-route">
        <div className="location">
          <span className="icon">📍</span>
          <span>{ride.origin}</span>
        </div>
        <div className="arrow">→</div>
        <div className="location">
          <span className="icon">📍</span>
          <span>{ride.destination}</span>
        </div>
      </div>

      <div className="ride-details">
        <p><strong>Departure:</strong> {formatDate(ride.departureTime)}</p>
        <p><strong>Available Seats:</strong> {ride.availableSeats}/{ride.totalSeats}</p>
        {ride.vehicleModel && <p><strong>Vehicle:</strong> {ride.vehicleModel}</p>}
      </div>

      <div className="driver-info">
        <img
          src={ride.driver.profilePicture || '/default-avatar.png'}
          alt={ride.driver.firstName}
          className="driver-avatar"
        />
        <div>
          <p className="driver-name">{ride.driver.firstName} {ride.driver.lastName}</p>
          <p className="driver-rating">⭐ {ride.driver.averageRating?.toFixed(1) || 'N/A'}</p>
        </div>
      </div>

      <Link to={`/rides/${ride.id}`} className="btn btn-secondary">
        View Details
      </Link>
    </div>
  );
};

export default RideCard;