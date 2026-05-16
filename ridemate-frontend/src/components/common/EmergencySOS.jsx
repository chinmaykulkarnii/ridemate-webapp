import React, { useState, useEffect } from 'react';
import { emergencyService } from '../../services/emergencyService';
import { useToast } from '../../context/ToastContext';
import { useAuth } from '../../context/AuthContext';
import '../../styles/EmergencySOS.css';

const EmergencySOS = ({ rideId, bookingId, onSOSTriggered }) => {
  const [showConfirmDialog, setShowConfirmDialog] = useState(false);
  const [isTriggering, setIsTriggering] = useState(false);
  const [activeSOS, setActiveSOS] = useState(null);
  const [currentLocation, setCurrentLocation] = useState(null);
  const toast = useToast();
  const { user } = useAuth();

  useEffect(() => {
    checkActiveSOS();
  }, []);

  const checkActiveSOS = async () => {
    try {
      const activeAlerts = await emergencyService.getActiveSOS();
      const myActiveAlert = activeAlerts.find(
        alert => alert.user.id === user.id && !alert.resolved
      );
      if (myActiveAlert) {
        setActiveSOS(myActiveAlert);
      }
    } catch (error) {
      console.error('Error checking active SOS:', error);
    }
  };

  const getCurrentLocation = () => {
    return new Promise((resolve, reject) => {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            resolve({
              latitude: position.coords.latitude,
              longitude: position.coords.longitude,
            });
          },
          (error) => {
            console.error('Geolocation error:', error);
            reject(error);
          }
        );
      } else {
        reject(new Error('Geolocation not supported'));
      }
    });
  };

  const handleSOSClick = () => {
    setShowConfirmDialog(true);
  };

  const handleConfirmSOS = async () => {
    setIsTriggering(true);
    setShowConfirmDialog(false);

    try {
      // Get current location
      let location = null;
      try {
        location = await getCurrentLocation();
        setCurrentLocation(location);
      } catch (error) {
        console.error('Could not get location:', error);
        // Continue without location
      }

      // Trigger SOS
      const sosData = {
        rideId: rideId,
        bookingId: bookingId,
        latitude: location?.latitude,
        longitude: location?.longitude,
        message: 'Emergency SOS triggered',
      };

      const result = await emergencyService.triggerSOS(sosData);
      setActiveSOS(result);
      toast.success('Emergency alert sent! Help is on the way.');

      // Notify parent component
      onSOSTriggered && onSOSTriggered(result);

      // In a real app, this would:
      // 1. Notify emergency contacts
      // 2. Alert nearby drivers/riders
      // 3. Contact local authorities if needed
      // 4. Share live location
    } catch (error) {
      console.error('Error triggering SOS:', error);
      toast.error('Failed to send emergency alert. Please call emergency services directly.');
    } finally {
      setIsTriggering(false);
    }
  };

  const handleResolveSOS = async () => {
    if (!activeSOS) return;

    try {
      await emergencyService.resolveSOS(activeSOS.id);
      setActiveSOS(null);
      toast.success('Emergency alert resolved');
    } catch (error) {
      console.error('Error resolving SOS:', error);
      toast.error('Failed to resolve emergency alert');
    }
  };

  const handleFalseAlarm = async () => {
    if (!activeSOS) return;

    try {
      await emergencyService.markFalseAlarm(activeSOS.id);
      setActiveSOS(null);
      toast.info('Emergency alert marked as false alarm');
    } catch (error) {
      console.error('Error marking false alarm:', error);
      toast.error('Failed to update emergency status');
    }
  };

  if (activeSOS) {
    return (
      <div className="emergency-active">
        <div className="emergency-active-header">
          <span className="emergency-icon-large">🚨</span>
          <h3>Emergency Alert Active</h3>
        </div>

        <div className="emergency-active-content">
          <p className="emergency-message">
            Your emergency alert has been sent. Help is on the way.
          </p>
          <p className="emergency-time">
            Triggered: {new Date(activeSOS.triggeredAt).toLocaleString()}
          </p>

          {currentLocation && (
            <div className="emergency-location">
              <strong>Your Location:</strong>
              <p>Lat: {currentLocation.latitude.toFixed(6)}</p>
              <p>Lon: {currentLocation.longitude.toFixed(6)}</p>
            </div>
          )}

          <div className="emergency-instructions">
            <h4>What to do:</h4>
            <ul>
              <li>Stay calm and in a safe location if possible</li>
              <li>Keep your phone with you</li>
              <li>Emergency contacts have been notified</li>
              <li>If in immediate danger, call local emergency services (100/112)</li>
            </ul>
          </div>

          <div className="emergency-actions">
            <button
              className="btn btn-resolve"
              onClick={handleResolveSOS}
            >
              Mark as Resolved
            </button>
            <button
              className="btn btn-false-alarm"
              onClick={handleFalseAlarm}
            >
              False Alarm
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <>
      <button
        className="emergency-sos-button"
        onClick={handleSOSClick}
        disabled={isTriggering}
        title="Emergency SOS - Press in case of emergency"
      >
        <span className="sos-icon">🚨</span>
        <span className="sos-text">SOS</span>
      </button>

      {showConfirmDialog && (
        <div className="emergency-dialog-overlay">
          <div className="emergency-dialog">
            <div className="dialog-header">
              <span className="dialog-icon">⚠️</span>
              <h3>Trigger Emergency Alert?</h3>
            </div>

            <div className="dialog-content">
              <p>
                This will send an emergency alert to:
              </p>
              <ul>
                <li>Your emergency contacts</li>
                <li>The driver (if in a ride)</li>
                <li>RideMate support team</li>
                <li>Share your live location</li>
              </ul>
              <p className="dialog-warning">
                Only use this in case of a real emergency.
              </p>
            </div>

            <div className="dialog-actions">
              <button
                className="btn btn-cancel"
                onClick={() => setShowConfirmDialog(false)}
              >
                Cancel
              </button>
              <button
                className="btn btn-emergency"
                onClick={handleConfirmSOS}
              >
                Confirm Emergency
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default EmergencySOS;
