import React from 'react';
import { Link } from 'react-router-dom';

const BookingCard = ({ booking, isDriver, onConfirm, onCancel }) => {
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  const getStatusColor = (status) => {
    const colors = {
      PENDING: 'orange',
      CONFIRMED: 'green',
      CANCELLED: 'red',
      COMPLETED: 'blue'
    };
    return colors[status] || 'gray';
  };

  return (
    <div className="booking-card">
      <div className="booking-header">
        <span className={`status ${getStatusColor(booking.status)}`}>
          {booking.status}
        </span>
        <span className="seats">{booking.seatsBooked} seat(s)</span>
      </div>

      <div className="booking-route">
        <div className="location">
          <span>📍 {booking.ride.origin}</span>
        </div>
        <div className="arrow">→</div>
        <div className="location">
          <span>📍 {booking.ride.destination}</span>
        </div>
      </div>

      <div className="booking-details">
        <p><strong>Departure:</strong> {formatDate(booking.ride.departureTime)}</p>
        <p><strong>Price:</strong> ₹{booking.ride.pricePerSeat * booking.seatsBooked}</p>
        <p><strong>Booked:</strong> {formatDate(booking.createdAt)}</p>
      </div>

      {isDriver ? (
        <div className="passenger-info">
          <p><strong>Passenger:</strong></p>
          <p>{booking.passenger.firstName} {booking.passenger.lastName}</p>
          <p>{booking.passenger.phoneNumber}</p>
        </div>
      ) : (
        <div className="driver-info">
          <p><strong>Driver:</strong></p>
          <p>{booking.ride.driver.firstName} {booking.ride.driver.lastName}</p>
          <p>⭐ {booking.ride.driver.averageRating?.toFixed(1) || 'N/A'}</p>
        </div>
      )}

      <div className="booking-actions">
        {booking.status === 'PENDING' && isDriver && (
          <button onClick={() => onConfirm(booking.id)} className="btn btn-success">
            Confirm
          </button>
        )}
        {(booking.status === 'PENDING' || booking.status === 'CONFIRMED') && (
          <button onClick={() => onCancel(booking.id)} className="btn btn-danger">
            Cancel
          </button>
        )}
        <Link to={`/rides/${booking.ride.id}`} className="btn btn-secondary">
          View Ride
        </Link>
      </div>
    </div>
  );
};

export default BookingCard;