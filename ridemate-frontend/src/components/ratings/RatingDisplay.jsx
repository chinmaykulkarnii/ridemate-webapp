import React, { useState, useEffect } from 'react';
import { ratingService } from '../../services/ratingService';

const RatingDisplay = ({ userId }) => {
  const [ratings, setRatings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadRatings();
  }, [userId]);

  const loadRatings = async () => {
    try {
      const data = await ratingService.getUserRatings(userId);
      setRatings(data);
    } catch (error) {
      console.error('Failed to load ratings:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Loading ratings...</div>;
  if (ratings.length === 0) return <div>No ratings yet</div>;

  // Calculate average by parameter
  const parameterAverages = {};
  ratings.forEach(rating => {
    if (!parameterAverages[rating.parameter]) {
      parameterAverages[rating.parameter] = { total: 0, count: 0 };
    }
    parameterAverages[rating.parameter].total += rating.stars;
    parameterAverages[rating.parameter].count += 1;
  });

  return (
    <div className="rating-display">
      <h4>Ratings Breakdown</h4>
      {Object.keys(parameterAverages).map(param => {
        const avg = (parameterAverages[param].total / parameterAverages[param].count).toFixed(1);
        return (
          <div key={param} className="rating-row">
            <span className="parameter-name">
              {param.replace(/_/g, ' ')}
            </span>
            <span className="rating-value">
              {avg} ⭐
            </span>
          </div>
        );
      })}
    </div>
  );
};

export default RatingDisplay;