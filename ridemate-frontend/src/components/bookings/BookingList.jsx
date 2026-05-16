import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import { bookingService } from '../../services/bookingService';
import BookingCard from './BookingCard';

const BookingList = () => {
  const { user } = useAuth();
  const toast = useToast();
  const [passengerBookings, setPassengerBookings] = useState([]);
  const [driverBookings, setDriverBookings] = useState([]);
  const [activeTab, setActiveTab] = useState('passenger');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadBookings();
  }, []);

  const loadBookings = async () => {
    try {
      const [passenger, driver] = await Promise.all([
        bookingService.getPassengerBookings(),
        bookingService.getDriverBookings()
      ]);
      setPassengerBookings(passenger);
      setDriverBookings(driver);
    } catch (error) {
      console.error('Failed to load bookings:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleConfirm = async (bookingId) => {
    try {
      await bookingService.confirmBooking(bookingId);
      loadBookings();
      toast.success('Booking confirmed successfully!');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to confirm booking');
    }
  };

  const handleCancel = async (bookingId) => {
    if (window.confirm('Are you sure you want to cancel this booking?')) {
      try {
        await bookingService.cancelBooking(bookingId);
        loadBookings();
        toast.success('Booking cancelled successfully!');
      } catch (error) {
        toast.error(error.response?.data?.message || 'Failed to cancel booking');
      }
    }
  };

  if (loading) return <div className="loading">Loading...</div>;

  const currentBookings = activeTab === 'passenger' ? passengerBookings : driverBookings;

  return (
    <div className="bookings-container">
      <h2>My Bookings</h2>

      <div className="tabs">
        <button
          className={`tab ${activeTab === 'passenger' ? 'active' : ''}`}
          onClick={() => setActiveTab('passenger')}
        >
          As Passenger ({passengerBookings.length})
        </button>
        <button
          className={`tab ${activeTab === 'driver' ? 'active' : ''}`}
          onClick={() => setActiveTab('driver')}
        >
          As Driver ({driverBookings.length})
        </button>
      </div>

      {currentBookings.length > 0 ? (
        <div className="bookings-grid">
          {currentBookings.map(booking => (
            <BookingCard
              key={booking.id}
              booking={booking}
              isDriver={activeTab === 'driver'}
              onConfirm={handleConfirm}
              onCancel={handleCancel}
            />
          ))}
        </div>
      ) : (
        <div className="empty-state">
          <p>No bookings found.</p>
        </div>
      )}
    </div>
  );
};

export default BookingList;