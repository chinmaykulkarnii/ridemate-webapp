import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { rideService } from '../../services/rideService';

const CreateRide = () => {
  const [formData, setFormData] = useState({
    origin: '',
    destination: '',
    departureTime: '',
    vehicleType: 'CAR',
    totalSeats: 1,
    pricePerSeat: '',
    vehicleModel: '',
    vehicleNumber: '',
    additionalInfo: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await rideService.createRide(formData);
      navigate('/my-rides');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create ride');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="create-ride-container">
      <h2>Post a New Ride</h2>
      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit} className="ride-form">
        <div className="form-row">
          <div className="form-group">
            <label>From *</label>
            <input
              type="text"
              name="origin"
              value={formData.origin}
              onChange={handleChange}
              required
              placeholder="Starting location"
            />
          </div>

          <div className="form-group">
            <label>To *</label>
            <input
              type="text"
              name="destination"
              value={formData.destination}
              onChange={handleChange}
              required
              placeholder="Destination"
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Departure Date & Time *</label>
            <input
              type="datetime-local"
              name="departureTime"
              value={formData.departureTime}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Vehicle Type *</label>
            <select
              name="vehicleType"
              value={formData.vehicleType}
              onChange={handleChange}
              required
            >
              <option value="BIKE">Bike</option>
              <option value="CAR">Car</option>
            </select>
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Available Seats *</label>
            <input
              type="number"
              name="totalSeats"
              value={formData.totalSeats}
              onChange={handleChange}
              min="1"
              max="10"
              required
            />
          </div>

          <div className="form-group">
            <label>Price per Seat (₹) *</label>
            <input
              type="number"
              name="pricePerSeat"
              value={formData.pricePerSeat}
              onChange={handleChange}
              min="0"
              required
              placeholder="Price per seat"
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Vehicle Model</label>
            <input
              type="text"
              name="vehicleModel"
              value={formData.vehicleModel}
              onChange={handleChange}
              placeholder="e.g., Honda City"
            />
          </div>

          <div className="form-group">
            <label>Vehicle Number</label>
            <input
              type="text"
              name="vehicleNumber"
              value={formData.vehicleNumber}
              onChange={handleChange}
              placeholder="e.g., MH-12-AB-1234"
            />
          </div>
        </div>

        <div className="form-group">
          <label>Additional Information</label>
          <textarea
            name="additionalInfo"
            value={formData.additionalInfo}
            onChange={handleChange}
            rows="4"
            placeholder="Any additional details about the ride..."
          />
        </div>

        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Creating...' : 'Post Ride'}
        </button>
      </form>
    </div>
  );
};

export default CreateRide;