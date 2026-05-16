import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { rideService } from '../../services/rideService';
import { bookingService } from '../../services/bookingService';
import { useAuth } from '../../context/AuthContext';

const RideDetails = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [ride, setRide] = useState(null);
  const [loading, setLoading] = useState(true);
  const [seatsToBook, setSeatsToBook] = useState(1);
  const [booking, setBooking] = useState(false);

  useEffect(() => {
    loadRide();
  }, [id]);

  const loadRide = async () => {
    try {
      const data = await rideService.getRideById(id);
      setRide(data);
    } catch (error) {
      console.error('Failed to load ride:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleBooking = async () => {
    if (!ride || seatsToBook < 1 || seatsToBook > ride.availableSeats) {
      return;
    }

    setBooking(true);
    try {
      await bookingService.createBooking({
        rideId: ride.id,
        seatsBooked: seatsToBook
      });
      alert('Booking request sent successfully!');
      navigate('/bookings');
    } catch (error) {
      alert('Failed to create booking: ' + error.response?.data?.message);
    } finally {
      setBooking(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (!ride) return <div className="error">Ride not found</div>;

  const isOwnRide = user?.id === ride.driver.id;

  return (
    <div className="ride-details-container">
      <div className="ride-details-card">
        <div className="ride-header">
          <h2>Ride Details</h2>
          <span className={`status ${ride.isActive ? 'active' : 'inactive'}`}>
            {ride.isActive ? 'Active' : 'Inactive'}
          </span>
        </div>

        <div className="ride-route-large">
          <div className="location-large">
            <h3>From</h3>
            <p>{ride.origin}</p>
          </div>
          <div className="arrow-large">→</div>
          <div className="location-large">
            <h3>To</h3>
            <p>{ride.destination}</p>
          </div>
        </div>

        <div className="ride-info-grid">
          <div className="info-item">
            <strong>Departure Time</strong>
            <p>{formatDate(ride.departureTime)}</p>
          </div>

          <div className="info-item">
            <strong>Vehicle Type</strong>
            <p>{ride.vehicleType}</p>
          </div>

          <div className="info-item">
            <strong>Available Seats</strong>
            <p>{ride.availableSeats} / {ride.totalSeats}</p>
          </div>

          <div className="info-item">
            <strong>Price per Seat</strong>
            <p>₹{ride.pricePerSeat}</p>
          </div>

          {ride.vehicleModel && (
            <div className="info-item">
              <strong>Vehicle Model</strong>
              <p>{ride.vehicleModel}</p>
            </div>
          )}

          {ride.vehicleNumber && (
            <div className="info-item">
              <strong>Vehicle Number</strong>
              <p>{ride.vehicleNumber}</p>
            </div>
          )}
        </div>

        {ride.additionalInfo && (
          <div className="additional-info">
            <strong>Additional Information</strong>
            <p>{ride.additionalInfo}</p>
          </div>
        )}

        <div className="driver-section">
          <h3>Driver Information</h3>
          <div className="driver-details">
            <img
              src={ride.driver.profilePicture || '/default-avatar.png'}
              alt={ride.driver.firstName}
              className="driver-avatar-large"
            />
            <div>
              <h4>{ride.driver.firstName} {ride.driver.lastName}</h4>
              <p>⭐ {ride.driver.averageRating?.toFixed(1) || 'N/A'} ({ride.driver.totalRatings} ratings)</p>
              <p>📧 {ride.driver.email}</p>
              <p>📞 {ride.driver.phoneNumber}</p>
            </div>
          </div>
        </div>

        {!isOwnRide && ride.isActive && ride.availableSeats > 0 && (
          <div className="booking-section">
            <h3>Book This Ride</h3>
            <div className="booking-form">
              <label>Number of Seats</label>
              <input
                type="number"
                value={seatsToBook}
                onChange={(e) => setSeatsToBook(parseInt(e.target.value))}
                min="1"
                max={ride.availableSeats}
              />
              <p className="total-price">
                Total: ₹{seatsToBook * ride.pricePerSeat}
              </p>
              <button
                onClick={handleBooking}
                className="btn btn-primary"
                disabled={booking}
              >
                {booking ? 'Booking...' : 'Book Now'}
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default RideDetails;