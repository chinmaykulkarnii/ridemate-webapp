import React, { useEffect, useState, useRef } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Polyline, useMap } from 'react-leaflet';
import L from 'leaflet';
import { trackingService } from '../../services/trackingService';
import { websocketService } from '../../services/websocketService';
import { useToast } from '../../context/ToastContext';
import 'leaflet/dist/leaflet.css';
import '../../styles/Map.css';

// Fix for default markers
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
});

const MapRecenter = ({ center }) => {
  const map = useMap();
  useEffect(() => {
    if (center) {
      map.setView(center, map.getZoom());
    }
  }, [center, map]);
  return null;
};

const RealTimeTracking = ({ rideId, ride }) => {
  const [currentLocation, setCurrentLocation] = useState(null);
  const [locationHistory, setLocationHistory] = useState([]);
  const [isTracking, setIsTracking] = useState(false);
  const [eta, setEta] = useState(null);
  const toast = useToast();
  const subscriptionRef = useRef(null);

  useEffect(() => {
    loadTrackingData();
    subscribeToLocationUpdates();

    return () => {
      if (subscriptionRef.current) {
        subscriptionRef.current.unsubscribe();
      }
    };
  }, [rideId]);

  const loadTrackingData = async () => {
    try {
      const [current, history] = await Promise.all([
        trackingService.getCurrentLocation(rideId),
        trackingService.getLocationHistory(rideId)
      ]);

      if (current) {
        setCurrentLocation(current);
        setIsTracking(true);
      }

      if (history && history.length > 0) {
        setLocationHistory(history);
      }
    } catch (error) {
      console.error('Error loading tracking data:', error);
      if (error.response?.status !== 404) {
        toast.error('Unable to load tracking data');
      }
    }
  };

  const subscribeToLocationUpdates = () => {
    if (websocketService.isConnected()) {
      subscriptionRef.current = websocketService.subscribe(
        `/topic/ride/${rideId}/location`,
        (message) => {
          const location = JSON.parse(message.body);
          setCurrentLocation(location);
          setLocationHistory(prev => [...prev, location]);
        }
      );
    }
  };

  const calculateETA = () => {
    if (!currentLocation || !ride?.destinationLatitude || !ride?.destinationLongitude) {
      return null;
    }

    // Simple ETA calculation based on straight-line distance
    // In production, use a routing API
    const R = 6371; // Earth's radius in km
    const dLat = (ride.destinationLatitude - currentLocation.latitude) * Math.PI / 180;
    const dLon = (ride.destinationLongitude - currentLocation.longitude) * Math.PI / 180;
    const a =
      Math.sin(dLat/2) * Math.sin(dLat/2) +
      Math.cos(currentLocation.latitude * Math.PI / 180) * Math.cos(ride.destinationLatitude * Math.PI / 180) *
      Math.sin(dLon/2) * Math.sin(dLon/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    const distance = R * c;

    // Assume average speed of 40 km/h
    const hours = distance / 40;
    const minutes = Math.round(hours * 60);

    return minutes;
  };

  useEffect(() => {
    if (currentLocation && ride) {
      const minutes = calculateETA();
      setEta(minutes);
    }
  }, [currentLocation, ride]);

  // Custom icons
  const carIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-violet.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
  });

  const destinationIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
  });

  if (!isTracking) {
    return (
      <div className="tracking-unavailable">
        <p>Live tracking is not available for this ride.</p>
        <p>The driver has not enabled location sharing.</p>
      </div>
    );
  }

  const center = currentLocation
    ? [currentLocation.latitude, currentLocation.longitude]
    : ride?.originLatitude && ride?.originLongitude
    ? [ride.originLatitude, ride.originLongitude]
    : [20.5937, 78.9629];

  const routePath = locationHistory.map(loc => [loc.latitude, loc.longitude]);

  return (
    <div className="real-time-tracking">
      <div className="tracking-info">
        {eta !== null && (
          <div className="eta-display">
            <span className="eta-label">Estimated Arrival:</span>
            <span className="eta-value">
              {eta < 60 ? `${eta} min` : `${Math.floor(eta / 60)}h ${eta % 60}m`}
            </span>
          </div>
        )}
        <div className="tracking-status">
          <span className="status-dot"></span>
          <span>Live Tracking Active</span>
        </div>
      </div>

      <div className="tracking-map-container">
        <MapContainer
          center={center}
          zoom={14}
          style={{ height: '100%', width: '100%' }}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />

          {currentLocation && <MapRecenter center={center} />}

          {/* Current Location Marker */}
          {currentLocation && (
            <Marker
              position={[currentLocation.latitude, currentLocation.longitude]}
              icon={carIcon}
            >
              <Popup>
                <strong>Driver's Current Location</strong>
                <br />
                Updated: {new Date(currentLocation.timestamp).toLocaleTimeString()}
                {currentLocation.speed && (
                  <>
                    <br />
                    Speed: {Math.round(currentLocation.speed)} km/h
                  </>
                )}
              </Popup>
            </Marker>
          )}

          {/* Destination Marker */}
          {ride?.destinationLatitude && ride?.destinationLongitude && (
            <Marker
              position={[ride.destinationLatitude, ride.destinationLongitude]}
              icon={destinationIcon}
            >
              <Popup>
                <strong>Destination</strong>
                <br />
                {ride.destination}
              </Popup>
            </Marker>
          )}

          {/* Route History */}
          {routePath.length > 1 && (
            <Polyline
              positions={routePath}
              color="blue"
              weight={4}
              opacity={0.7}
            />
          )}
        </MapContainer>
      </div>
    </div>
  );
};

export default RealTimeTracking;
