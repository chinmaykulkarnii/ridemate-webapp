import React, { useState } from 'react';
import { ratingService } from '../../services/ratingService';
import { useToast } from '../../context/ToastContext';

const RatingForm = ({ booking, onClose, onSuccess }) => {
  const toast = useToast();
  const [ratings, setRatings] = useState({});
  const [loading, setLoading] = useState(false);

  const isDriver = booking.ride.driver.id === booking.passenger.id;

  const parameters = isDriver
    ? ['BEHAVIOR', 'PASSENGER_PUNCTUALITY', 'PASSENGER_COMMUNICATION', 'CLEANLINESS']
    : ['DRIVING_SKILLS', 'SAFETY', 'COMMUNICATION', 'PUNCTUALITY', 'VEHICLE_CONDITION'];

  const handleRating = (parameter, stars) => {
    setRatings({
      ...ratings,
      [parameter]: stars
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (Object.keys(ratings).length !== parameters.length) {
      toast.error('Please rate all parameters');
      return;
    }

    setLoading(true);
    try {
      const rateeId = isDriver ? booking.passenger.id : booking.ride.driver.id;

      await ratingService.createRatings({
        bookingId: booking.id,
        rateeId: rateeId,
        ratings: ratings
      });

      toast.success('Rating submitted successfully!');
      onSuccess();
      onClose();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to submit rating');
    } finally {
      setLoading(false);
    }
  };

  const formatParameter = (param) => {
    return param.replace(/_/g, ' ').toLowerCase()
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  };

  return (
    <div className="rating-modal">
      <div className="rating-form">
        <h3>Rate {isDriver ? 'Passenger' : 'Driver'}</h3>
        <p className="rating-subtitle">
          Rating: {isDriver
            ? `${booking.passenger.firstName} ${booking.passenger.lastName}`
            : `${booking.ride.driver.firstName} ${booking.ride.driver.lastName}`
          }
        </p>

        <form onSubmit={handleSubmit}>
          {parameters.map(param => (
            <div key={param} className="rating-item">
              <label>{formatParameter(param)}</label>
              <div className="stars">
                {[1, 2, 3, 4, 5].map(star => (
                  <span
                    key={star}
                    className={`star ${ratings[param] >= star ? 'filled' : ''}`}
                    onClick={() => handleRating(param, star)}
                  >
                    ⭐
                  </span>
                ))}
              </div>
            </div>
          ))}

          <div className="button-group">
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Submitting...' : 'Submit Rating'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RatingForm;