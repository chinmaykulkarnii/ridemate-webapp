import React, { useEffect, useRef } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Polyline, useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import '../../styles/Map.css';

// Fix for default markers not showing
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
});

const MapUpdater = ({ bounds }) => {
  const map = useMap();

  useEffect(() => {
    if (bounds && bounds.length > 0) {
      map.fitBounds(bounds, { padding: [50, 50] });
    }
  }, [bounds, map]);

  return null;
};

const RideSearchMap = ({ rides, originCoords, destinationCoords, onRideSelect }) => {
  const mapRef = useRef();

  // Calculate map bounds to fit all markers
  const getBounds = () => {
    const bounds = [];

    if (originCoords) {
      bounds.push([originCoords.latitude, originCoords.longitude]);
    }

    if (destinationCoords) {
      bounds.push([destinationCoords.latitude, destinationCoords.longitude]);
    }

    rides.forEach(ride => {
      if (ride.originLatitude && ride.originLongitude) {
        bounds.push([ride.originLatitude, ride.originLongitude]);
      }
      if (ride.destinationLatitude && ride.destinationLongitude) {
        bounds.push([ride.destinationLatitude, ride.destinationLongitude]);
      }
    });

    return bounds.length > 0 ? bounds : null;
  };

  const bounds = getBounds();
  const center = bounds && bounds.length > 0
    ? [bounds[0][0], bounds[0][1]]
    : [20.5937, 78.9629]; // Center of India as default

  // Custom icons
  const originIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
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

  const rideIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
  });

  return (
    <div className="ride-search-map-container">
      <MapContainer
        center={center}
        zoom={13}
        style={{ height: '100%', width: '100%' }}
        ref={mapRef}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        {bounds && <MapUpdater bounds={bounds} />}

        {/* Search Origin Marker */}
        {originCoords && (
          <Marker
            position={[originCoords.latitude, originCoords.longitude]}
            icon={originIcon}
          >
            <Popup>
              <strong>Your Origin</strong>
              <br />
              {originCoords.address || 'Starting point'}
            </Popup>
          </Marker>
        )}

        {/* Search Destination Marker */}
        {destinationCoords && (
          <Marker
            position={[destinationCoords.latitude, destinationCoords.longitude]}
            icon={destinationIcon}
          >
            <Popup>
              <strong>Your Destination</strong>
              <br />
              {destinationCoords.address || 'End point'}
            </Popup>
          </Marker>
        )}

        {/* Search route line */}
        {originCoords && destinationCoords && (
          <Polyline
            positions={[
              [originCoords.latitude, originCoords.longitude],
              [destinationCoords.latitude, destinationCoords.longitude]
            ]}
            color="green"
            weight={3}
            opacity={0.5}
            dashArray="10, 10"
          />
        )}

        {/* Available Rides Markers */}
        {rides && rides.map(ride => (
          <React.Fragment key={ride.id}>
            {ride.originLatitude && ride.originLongitude && (
              <Marker
                position={[ride.originLatitude, ride.originLongitude]}
                icon={rideIcon}
                eventHandlers={{
                  click: () => onRideSelect && onRideSelect(ride)
                }}
              >
                <Popup>
                  <div className="ride-popup">
                    <strong>{ride.origin}</strong>
                    <p>to {ride.destination}</p>
                    <p><strong>Driver:</strong> {ride.driver?.firstName} {ride.driver?.lastName}</p>
                    <p><strong>Price:</strong> ₹{ride.price}</p>
                    <p><strong>Seats:</strong> {ride.availableSeats}</p>
                    <p><strong>Date:</strong> {new Date(ride.departureDate).toLocaleDateString()}</p>
                    {onRideSelect && (
                      <button
                        className="popup-view-btn"
                        onClick={() => onRideSelect(ride)}
                      >
                        View Details
                      </button>
                    )}
                  </div>
                </Popup>
              </Marker>
            )}

            {/* Ride route line */}
            {ride.originLatitude && ride.originLongitude &&
             ride.destinationLatitude && ride.destinationLongitude && (
              <Polyline
                positions={[
                  [ride.originLatitude, ride.originLongitude],
                  [ride.destinationLatitude, ride.destinationLongitude]
                ]}
                color="blue"
                weight={2}
                opacity={0.6}
              />
            )}
          </React.Fragment>
        ))}
      </MapContainer>
    </div>
  );
};

export default RideSearchMap;
