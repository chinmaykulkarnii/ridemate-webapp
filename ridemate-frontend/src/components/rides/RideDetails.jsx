import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { rideService } from '../../services/rideService';
import { bookingService } from '../../services/bookingService';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import PaymentForm from '../common/PaymentForm';
import RealTimeTracking from '../common/RealTimeTracking';
import EmergencySOS from '../common/EmergencySOS';
import RideSearchMap from '../common/RideSearchMap';

const RideDetailsEnhanced = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();

  const [ride, setRide] = useState(null);
  const [loading, setLoading] = useState(true);
  const [seatsToBook, setSeatsToBook] = useState(1);
  const [booking, setBooking] = useState(false);
  const [showPayment, setShowPayment] = useState(false);
  const [currentBooking, setCurrentBooking] = useState(null);
  const [showTracking, setShowTracking] = useState(false);
  const [activeTab, setActiveTab] = useState('details'); // 'details', 'tracking', 'map'

  useEffect(() => {
    loadRide();
  }, [id]);

  const loadRide = async () => {
    try {
      const data = await rideService.getRideById(id);
      setRide(data);
    } catch (error) {
      console.error('Failed to load ride:', error);
      toast.error('Failed to load ride details');
    } finally {
      setLoading(false);
    }
  };

  const handleBooking = async () => {
    if (!ride || seatsToBook < 1 || seatsToBook > ride.availableSeats) {
      toast.error('Invalid number of seats');
      return;
    }

    setBooking(true);
    try {
      const newBooking = await bookingService.createBooking({
        rideId: ride.id,
        seatsBooked: seatsToBook
      });

      toast.success('Booking created successfully!');
      setCurrentBooking(newBooking);
      setShowPayment(true); // Show payment form
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to create booking');
    } finally {
      setBooking(false);
    }
  };

  const handlePaymentSuccess = (paymentResult) => {
    toast.success('Payment completed successfully!');
    setShowPayment(false);
    navigate('/bookings');
  };

  const handlePaymentCancel = () => {
    setShowPayment(false);
    toast.info('Payment cancelled. You can complete payment later from bookings page.');
    navigate('/bookings');
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (!ride) return <div className="error">Ride not found</div>;

  const isOwnRide = user?.id === ride.driver.id;
  const totalPrice = seatsToBook * ride.pricePerSeat;

  // Show payment modal
  if (showPayment && currentBooking) {
    return (
      <PaymentForm
        booking={currentBooking}
        amount={totalPrice}
        onSuccess={handlePaymentSuccess}
        onCancel={handlePaymentCancel}
      />
    );
  }

  return (
    <div className="ride-details-container">
      {/* Emergency SOS Button (for booked passengers) */}
      {!isOwnRide && currentBooking && (
        <EmergencySOS
          rideId={ride.id}
          bookingId={currentBooking.id}
          onSOSTriggered={(sosData) => {
            toast.warning('Emergency alert sent!');
          }}
        />
      )}

      <div className="ride-details-card">
        <div className="ride-header">
          <h2>Ride Details</h2>
          <div className="header-badges">
            <span className={`status ${ride.isActive ? 'active' : 'inactive'}`}>
              {ride.isActive ? 'Active' : 'Inactive'}
            </span>
            {ride.womenOnly && <span className="badge">Women Only</span>}
            {ride.petFriendly && <span className="badge">Pet Friendly</span>}
            {ride.driver.verified && <span className="badge verified">Verified Driver</span>}
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="tab-navigation">
          <button
            className={`tab-btn ${activeTab === 'details' ? 'active' : ''}`}
            onClick={() => setActiveTab('details')}
          >
            Details
          </button>
          <button
            className={`tab-btn ${activeTab === 'map' ? 'active' : ''}`}
            onClick={() => setActiveTab('map')}
          >
            Map View
          </button>
          <button
            className={`tab-btn ${activeTab === 'tracking' ? 'active' : ''}`}
            onClick={() => setActiveTab('tracking')}
          >
            Live Tracking
          </button>
        </div>

        {/* Tab Content */}
        {activeTab === 'details' && (
          <>
            <div className="ride-route-large">
              <div className="location-large">
                <h3>From</h3>
                <p>{ride.origin}</p>
                {ride.originLatitude && (
                  <small>📍 {ride.originLatitude.toFixed(4)}, {ride.originLongitude.toFixed(4)}</small>
                )}
              </div>
              <div className="arrow-large">→</div>
              <div className="location-large">
                <h3>To</h3>
                <p>{ride.destination}</p>
                {ride.destinationLatitude && (
                  <small>📍 {ride.destinationLatitude.toFixed(4)}, {ride.destinationLongitude.toFixed(4)}</small>
                )}
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

              {ride.distance && (
                <div className="info-item">
                  <strong>Distance</strong>
                  <p>{ride.distance} km</p>
                </div>
              )}

              {ride.estimatedDuration && (
                <div className="info-item">
                  <strong>Estimated Duration</strong>
                  <p>{ride.estimatedDuration} min</p>
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
                  <h4>
                    {ride.driver.firstName} {ride.driver.lastName}
                    {ride.driver.verified && <span className="verified-badge">✓</span>}
                  </h4>
                  <p>⭐ {ride.driver.averageRating?.toFixed(1) || 'N/A'} ({ride.driver.totalRatings} ratings)</p>
                  <p>📧 {ride.driver.email}</p>
                  <p>📞 {ride.driver.phoneNumber}</p>
                  {ride.driver.totalRidesOffered && (
                    <p>🚗 {ride.driver.totalRidesOffered} rides offered</p>
                  )}
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
                    onChange={(e) => setSeatsToBook(Math.max(1, Math.min(ride.availableSeats, parseInt(e.target.value) || 1)))}
                    min="1"
                    max={ride.availableSeats}
                  />
                  <div className="price-breakdown">
                    <div className="price-row">
                      <span>Price per seat:</span>
                      <span>₹{ride.pricePerSeat}</span>
                    </div>
                    <div className="price-row">
                      <span>Number of seats:</span>
                      <span>× {seatsToBook}</span>
                    </div>
                    <div className="price-row total">
                      <strong>Total:</strong>
                      <strong>₹{totalPrice}</strong>
                    </div>
                  </div>
                  <button
                    onClick={handleBooking}
                    className="btn btn-primary"
                    disabled={booking}
                  >
                    {booking ? 'Processing...' : 'Book Now'}
                  </button>
                </div>
              </div>
            )}

            {isOwnRide && (
              <div className="own-ride-actions">
                <p>This is your ride. You can manage it from "My Rides" page.</p>
                <button className="btn btn-secondary" onClick={() => navigate('/my-rides')}>
                  Go to My Rides
                </button>
              </div>
            )}
          </>
        )}

        {activeTab === 'map' && (
          <div className="map-tab">
            <RideSearchMap
              rides={[ride]}
              originCoords={ride.originLatitude && ride.originLongitude ? {
                latitude: ride.originLatitude,
                longitude: ride.originLongitude,
                address: ride.origin
              } : null}
              destinationCoords={ride.destinationLatitude && ride.destinationLongitude ? {
                latitude: ride.destinationLatitude,
                longitude: ride.destinationLongitude,
                address: ride.destination
              } : null}
              onRideSelect={() => {}}
            />
          </div>
        )}

        {activeTab === 'tracking' && (
          <div className="tracking-tab">
            <RealTimeTracking rideId={ride.id} ride={ride} />
          </div>
        )}
      </div>
    </div>
  );
};

export default RideDetailsEnhanced;
