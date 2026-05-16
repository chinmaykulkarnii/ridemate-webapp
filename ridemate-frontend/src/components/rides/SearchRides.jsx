import React, { useState } from 'react';
import { rideService } from '../../services/rideService';
import { useToast } from '../../context/ToastContext';
import RideCard from './RideCard';
import { useNavigate } from 'react-router-dom';
import './SearchRides.css'; // We'll create this

const SearchRides = () => {
  const [filters, setFilters] = useState({
    origin: '',
    destination: '',
    departureTime: '',
    seatsRequired: 1,
    vehicleType: '',
    maxPrice: '',
    womenOnly: false,
    petFriendly: false,
    verifiedDriversOnly: false,
  });

  const [rides, setRides] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);
  const toast = useToast();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFilters({
      ...filters,
      [name]: type === 'checkbox' ? checked : value
    });
  };

  const handleSearch = async (e) => {
    e.preventDefault();

    if (!filters.origin || !filters.destination) {
      toast.error('Please enter both origin and destination');
      return;
    }

    setLoading(true);
    setSearched(true);

    try {
      const searchFilters = {
        origin: filters.origin,
        destination: filters.destination,
        departureTime: filters.departureTime,
        seatsRequired: filters.seatsRequired
      };

      if (filters.vehicleType) searchFilters.vehicleType = filters.vehicleType;
      if (filters.maxPrice) searchFilters.maxPrice = filters.maxPrice;
      if (filters.womenOnly) searchFilters.womenOnly = filters.womenOnly;
      if (filters.petFriendly) searchFilters.petFriendly = filters.petFriendly;
      if (filters.verifiedDriversOnly) searchFilters.verifiedDriversOnly = filters.verifiedDriversOnly;

      const data = await rideService.searchRides(searchFilters);

      let ridesArray = [];
      if (Array.isArray(data)) {
        ridesArray = data;
      } else if (data && Array.isArray(data.rides)) {
        ridesArray = data.rides;
      }

      setRides(ridesArray);

      if (ridesArray.length === 0) {
        toast.info('No rides found matching your criteria');
      } else {
        toast.success(`Found ${ridesArray.length} ride(s)`);
      }
    } catch (error) {
      console.error('Search failed:', error);
      toast.error('Search failed. Please try again.');
      setRides([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="search-rides-page">
      <div className="search-hero">
        <h1 className="search-title">Find Your Perfect Ride</h1>
        <p className="search-subtitle">Search thousands of rides. Save money. Travel green.</p>
      </div>

      <div className="search-container">
        <form onSubmit={handleSearch} className="search-form-card">
          {/* Main Search Fields */}
          <div className="search-main-fields">
            <div className="form-group-icon">
              <span className="input-icon">📍</span>
              <div className="form-group">
                <label>From</label>
                <input
                  type="text"
                  name="origin"
                  value={filters.origin}
                  onChange={handleChange}
                  placeholder="Enter pickup location"
                  required
                  className="form-input"
                />
              </div>
            </div>

            <div className="form-group-icon">
              <span className="input-icon">🎯</span>
              <div className="form-group">
                <label>To</label>
                <input
                  type="text"
                  name="destination"
                  value={filters.destination}
                  onChange={handleChange}
                  placeholder="Enter destination"
                  required
                  className="form-input"
                />
              </div>
            </div>
          </div>

          {/* Secondary Fields */}
          <div className="search-secondary-fields">
            <div className="form-group">
              <label>📅 Departure Date & Time</label>
              <input
                type="datetime-local"
                name="departureTime"
                value={filters.departureTime}
                onChange={handleChange}
                required
                className="form-input"
              />
            </div>

            <div className="form-group">
              <label>👤 Seats Required</label>
              <input
                type="number"
                name="seatsRequired"
                value={filters.seatsRequired}
                onChange={handleChange}
                min="1"
                required
                className="form-input"
              />
            </div>

            <div className="form-group">
              <label>🚗 Vehicle Type</label>
              <select
                name="vehicleType"
                value={filters.vehicleType}
                onChange={handleChange}
                className="form-input"
              >
                <option value="">All Types</option>
                <option value="BIKE">Bike</option>
                <option value="CAR">Car</option>
              </select>
            </div>

            <div className="form-group">
              <label>💰 Max Price per Seat</label>
              <input
                type="number"
                name="maxPrice"
                value={filters.maxPrice}
                onChange={handleChange}
                placeholder="Any price"
                className="form-input"
              />
            </div>
          </div>

          {/* Advanced Filters */}
          <div className="advanced-filters-section">
            <h3 className="filters-title">Preferences</h3>
            <div className="filters-grid">
              <label className="filter-checkbox">
                <input
                  type="checkbox"
                  name="womenOnly"
                  checked={filters.womenOnly}
                  onChange={handleChange}
                />
                <span className="checkbox-custom"></span>
                <span className="checkbox-label">👩 Women Only</span>
              </label>

              <label className="filter-checkbox">
                <input
                  type="checkbox"
                  name="petFriendly"
                  checked={filters.petFriendly}
                  onChange={handleChange}
                />
                <span className="checkbox-custom"></span>
                <span className="checkbox-label">🐕 Pet Friendly</span>
              </label>

              <label className="filter-checkbox">
                <input
                  type="checkbox"
                  name="verifiedDriversOnly"
                  checked={filters.verifiedDriversOnly}
                  onChange={handleChange}
                />
                <span className="checkbox-custom"></span>
                <span className="checkbox-label">✓ Verified Drivers Only</span>
              </label>
            </div>
          </div>

          {/* Search Button */}
          <button
            type="submit"
            className="search-btn"
            disabled={loading}
          >
            {loading ? (
              <>
                <span className="spinner"></span>
                Searching...
              </>
            ) : (
              <>
                🔍 Search Rides
              </>
            )}
          </button>
        </form>

        {/* Results Section */}
        {searched && (
          <div className="search-results">
            <div className="results-header">
              <h2>
                {rides.length > 0
                  ? `Found ${rides.length} Ride${rides.length > 1 ? 's' : ''}`
                  : 'No Rides Found'}
              </h2>
            </div>

            {rides.length > 0 ? (
              <div className="rides-grid">
                {rides.map(ride => (
                  <RideCard key={ride.id} ride={ride} />
                ))}
              </div>
            ) : (
              <div className="no-results">
                <div className="no-results-icon">🚗💨</div>
                <h3>No rides match your search</h3>
                <p>Try adjusting your filters or search criteria</p>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchRides;