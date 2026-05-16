import React, { useState, useEffect, useRef } from 'react';
import { locationService } from '../../services/locationService';
import '../../styles/LocationAutocomplete.css';

const LocationAutocomplete = ({ value, onChange, placeholder, required }) => {
  const [query, setQuery] = useState(value || '');
  const [suggestions, setSuggestions] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const wrapperRef = useRef(null);
  const timeoutRef = useRef(null);

  // Sync internal query state with value prop
  useEffect(() => {
    setQuery(value || '');
  }, [value]);

  // Handle clicks outside component
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        setShowSuggestions(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Debounced search
  useEffect(() => {
    if (query.length < 3) {
      setSuggestions([]);
      return;
    }

    // Clear previous timeout
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }

    // Set new timeout for debouncing
    timeoutRef.current = setTimeout(async () => {
      setIsLoading(true);
      try {
        const results = await locationService.searchLocations(query);
        setSuggestions(results || []);
        setShowSuggestions(true);
      } catch (error) {
        console.error('Error searching locations:', error);
        setSuggestions([]);
      } finally {
        setIsLoading(false);
      }
    }, 300);

    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, [query]);

  const handleInputChange = (e) => {
    const newValue = e.target.value;
    setQuery(newValue);
  };

  const handleSelectSuggestion = (suggestion) => {
    const displayName = suggestion.display_name || suggestion.name || suggestion.formatted;
    setQuery(displayName);
    setShowSuggestions(false);

    // Pass the selected location data to parent
    onChange({
      address: displayName,
      latitude: parseFloat(suggestion.lat || suggestion.latitude),
      longitude: parseFloat(suggestion.lon || suggestion.longitude),
    });
  };

  const handleGetCurrentLocation = () => {
    setIsLoading(true);
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          try {
            const { latitude, longitude } = position.coords;
            const result = await locationService.reverseGeocode(latitude, longitude);
            const displayName = result.display_name || result.formatted || 'Current Location';
            setQuery(displayName);
            onChange({
              address: displayName,
              latitude,
              longitude,
            });
          } catch (error) {
            console.error('Error reverse geocoding:', error);
          } finally {
            setIsLoading(false);
          }
        },
        (error) => {
          console.error('Error getting location:', error);
          alert('Unable to get your current location. Please enter manually.');
          setIsLoading(false);
        }
      );
    } else {
      alert('Geolocation is not supported by your browser');
      setIsLoading(false);
    }
  };

  return (
    <div className="location-autocomplete" ref={wrapperRef}>
      <div className="location-input-wrapper">
        <input
          type="text"
          className="location-input"
          value={query}
          onChange={handleInputChange}
          onFocus={() => Array.isArray(suggestions) && suggestions.length > 0 && setShowSuggestions(true)}
          placeholder={placeholder || 'Enter location'}
          required={required}
        />
        <button
          type="button"
          className="location-btn"
          onClick={handleGetCurrentLocation}
          disabled={isLoading}
          title="Use current location"
        >
          {isLoading ? '⟳' : '📍'}
        </button>
      </div>

      {showSuggestions && Array.isArray(suggestions) && suggestions.length > 0 && (
        <div className="location-suggestions">
          {suggestions.map((suggestion, index) => (
            <div
              key={index}
              className="location-suggestion-item"
              onClick={() => handleSelectSuggestion(suggestion)}
            >
              <span className="suggestion-icon">📍</span>
              <div className="suggestion-text">
                <div className="suggestion-name">
                  {suggestion.display_name || suggestion.name || suggestion.formatted}
                </div>
                {suggestion.type && (
                  <div className="suggestion-type">{suggestion.type}</div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {isLoading && (
        <div className="location-loading">Searching...</div>
      )}
    </div>
  );
};

export default LocationAutocomplete;
